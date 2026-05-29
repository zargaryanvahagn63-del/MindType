package vahagn.zargaryan.mindtype.tasks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vahagn.zargaryan.mindtype.tasks.achievents.Achievement;
import vahagn.zargaryan.mindtype.tasks.achievents.AchievementAdapter;
import vahagn.zargaryan.mindtype.tasks.achievents.AchievementManager;
import vahagn.zargaryan.mindtype.tasks.quotes.QuoteManager;
import vahagn.zargaryan.mindtype.R;

/**
 * Фрагмент раздела "Задания".
 * Объединяет в себе блок ежедневной цитаты (QuoteManager),
 * список пошаговых квестов цепочки тестов и систему наград (ачивок).
 */
public class TasksFragment extends Fragment {

    private static final String TAG = "TasksFragment";

    // Элементы UI цитаты дня
    private TextView tvQuoteText, tvQuoteAuthor;
    private QuoteManager quoteManager;

    // Списки и адаптеры
    private RecyclerView rvDailyTasks, rvAchievements;
    private DailyTasksAdapter dailyTasksAdapter;
    private AchievementAdapter achievementAdapter;

    // Ссылки на базу данных Firebase
    private DatabaseReference userRef;
    private ValueEventListener userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Инициализация менеджера цитат
        quoteManager = new QuoteManager();

        // 2. Инициализация View и настройка списков
        initViews(view);
        setupRecyclerViews();

        // 3. Асинхронная загрузка данных
        loadDailyQuote();
        loadTasksAndAchievementsData();
    }

    private void initViews(View view) {
        tvQuoteText = view.findViewById(R.id.tvQuoteText);
        tvQuoteAuthor = view.findViewById(R.id.tvQuoteAuthor);
        rvDailyTasks = view.findViewById(R.id.rvDailyTasks);
        rvAchievements = view.findViewById(R.id.rvAchievements);
    }

    private void setupRecyclerViews() {
        // Настройка списка заданий (вертикальный)
        rvDailyTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        dailyTasksAdapter = new DailyTasksAdapter();
        rvDailyTasks.setAdapter(dailyTasksAdapter);

        // Настройка списка достижений (вертикальный, NestedScrolling отключен для плавности в ScrollView)
        rvAchievements.setLayoutManager(new LinearLayoutManager(getContext()));
        achievementAdapter = new AchievementAdapter();
        rvAchievements.setAdapter(achievementAdapter);
        rvAchievements.setNestedScrollingEnabled(false);
    }

    /**
     * Безопасная загрузка ежедневной цитаты с защитой от утечек памяти и крашей.
     */
    private void loadDailyQuote() {
        Log.d(TAG, "Начинаем загрузку цитаты дня...");
        quoteManager.fetchDailyQuote(new QuoteManager.OnQuoteFetched() {
            @Override
            public void onSuccess(String text, String author) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded() && tvQuoteText != null && tvQuoteAuthor != null) {
                            tvQuoteText.setText("«" + text.trim() + "»");
                            tvQuoteAuthor.setText(author.isEmpty() ? "— Неизвестный" : "— " + author);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Ошибка загрузки цитаты: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded() && tvQuoteText != null && tvQuoteAuthor != null) {
                            tvQuoteText.setText("Великие мысли приходят тем, кто их ищет.");
                            tvQuoteAuthor.setText("— MindType");
                        }
                    });
                }
            }
        });
    }

    /**
     * Загружает прогресс пользователя из Firebase, автоматически формирует
     * список невыполненных квестов и сверяет статус разблокировки ачивок.
     */
    private void loadTasksAndAchievementsData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                // --- ЧАСТЬ 1: ГЕНЕРАЦИЯ СПИСКА ЗАДАНИЙ (RPG КВЕСТЫ) ---
                List<DailyTask> tasksList = new ArrayList<>();

                // Проверяем готовность каждого этапа цепочки тестов
                boolean mbtiDone = snapshot.child("mbtiDone").exists() || snapshot.child("mbtiType").exists();
                boolean eqDone = snapshot.child("eqDone").exists() || snapshot.child("eqScore").exists();
                boolean big5Part1Done = snapshot.child("big5_part1Done").exists() || snapshot.child("big5_part1Score").exists();
                boolean varkDone = snapshot.child("varkDone").exists() || snapshot.child("varkScore").exists();
                boolean big5Part2Done = snapshot.child("big5_part2Done").exists() || snapshot.child("big5_part2Score").exists();
                boolean dark3Done = snapshot.child("dark3Done").exists() || snapshot.child("dark3Score").exists();

                // Динамически наполняем квесты в зависимости от прогресса цепочки
                tasksList.add(new DailyTask("Пройти тест MBTI", "Определите свой 4-буквенный психологический код.", mbtiDone));

                DailyTask eqTask = new DailyTask("Оценить Эмоциональный Интеллект", "Пройдите исследование EQ.", eqDone);
                if (!mbtiDone) eqTask.setLocked(true, "Сначала завершите тест MBTI");
                tasksList.add(eqTask);

                DailyTask big5Part1Task = new DailyTask("Большая Пятерка: Часть 1", "Пройдите оценку первых трех шкал характера.", big5Part1Done);
                if (!eqDone) big5Part1Task.setLocked(true, "Сначала завершите тест EQ");
                tasksList.add(big5Part1Task);

                DailyTask varkTask = new DailyTask("Стиль обучения (VARK)", "Узнайте свой ведущий канал восприятия информации.", varkDone);
                if (!big5Part1Done)
                    varkTask.setLocked(true, "Сначала пройдите Большую Пятерку ч. 1");
                tasksList.add(varkTask);

                DailyTask big5Part2Task = new DailyTask("Большая Пятерка: Часть 2", "Завершите тест личности и постройте Spider Chart.", big5Part2Done);
                if (!varkDone) big5Part2Task.setLocked(true, "Сначала завершите исследование VARK");
                tasksList.add(big5Part2Task);

                DailyTask dark3Task = new DailyTask("Исследовать Темную Триаду", "Измерьте уровень макиавеллизма, нарциссизма и психопатии.", dark3Done);
                if (!big5Part2Done)
                    dark3Task.setLocked(true, "Сначала завершите Большую Пятерку ч. 2");
                tasksList.add(dark3Task);

                dailyTasksAdapter.setData(tasksList);


                // --- ЧАСТЬ 2: СИНХРОНИЗАЦИЯ ДОСТИЖЕНИЙ (МЕДАЛИ) ---
                List<Achievement> achievementsList = AchievementManager.getBaseAchievements();
                DataSnapshot achSnapshot = snapshot.child("achievements");

                for (Achievement achievement : achievementsList) {
                    if (achSnapshot.hasChild(achievement.getId())) {
                        achievement.setUnlocked(true);
                    }
                }
                achievementAdapter.setData(achievementsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Ошибка чтения Firebase: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Чистим слушатели Firebase для предотвращения утечек памяти
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }

    // =========================================================================
    // ВНУТРЕННИЕ КЛАССЫ И МОДЕЛИ ДЛЯ ДИНАМИЧЕСКИХ ЗАДАНИЙ (DAILY TASKS)
    // =========================================================================
}