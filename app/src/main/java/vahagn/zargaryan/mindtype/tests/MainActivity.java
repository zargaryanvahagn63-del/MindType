package vahagn.zargaryan.mindtype.tests;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.result.ResultActivity;
import vahagn.zargaryan.mindtype.analyzers.BaseAnalyzer;
import vahagn.zargaryan.mindtype.analyzers.DarkTriadAnalyzer;
import vahagn.zargaryan.mindtype.analyzers.EQAnalyzer;
import vahagn.zargaryan.mindtype.analyzers.MBTIAnalyzer;
import vahagn.zargaryan.mindtype.analyzers.PersonalityAnalyzer;
import vahagn.zargaryan.mindtype.analyzers.VarkAnalyzer;
import vahagn.zargaryan.mindtype.tasks.XpManager;

/**
 * Главная активность для прохождения тестов.
 * Отвечает за отображение списка вопросов, отслеживание прогресса и отправку результатов.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;      // Список вопросов
    private TextView tvProgress;        // Текстовый индикатор прогресса (напр. 5/20)
    private ProgressBar progress;       // Горизонтальная полоса прогресса
    private Button btnFinish;           // Кнопка завершения теста

    private QuestionAdapter adapter;    // Адаптер для управления элементами списка
    private List<Question> questions;   // Список объектов вопросов
    private BaseAnalyzer analyzer;      // Анализатор, специфичный для текущего теста
    private TestType type;              // Тип текущего теста
    private int total;                  // Общее количество вопросов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI(); // Привязка View

        // 1. Извлекаем тип теста из переданного Intent
        String typeStr = getIntent().getStringExtra("type");
        if (typeStr == null) { finish(); return; }

        try {
            // Приводим к верхнему регистру для предотвращения ошибок сопоставления
            type = TestType.valueOf(typeStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            finish();
            return;
        }

        // 2. Инициализируем анализатор в зависимости от выбранного типа теста
        initAnalyzer();

        if (analyzer != null) {
            questions = analyzer.getQuestions();
            if (questions == null || questions.isEmpty()) { finish(); return; }
            total = questions.size();
        } else {
            finish();
            return;
        }

        // 3. Настройка RecyclerView: менеджер компоновки и адаптер
        adapter = new QuestionAdapter(questions, this::updateProgress);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // Установка начального состояния
        updateProgress(0);
        btnFinish.setOnClickListener(v -> finishTest());
    }

    /**
     * Инициализация компонентов интерфейса.
     */
    private void initUI() {
        recycler = findViewById(R.id.recycler);
        tvProgress = findViewById(R.id.tvProgress);
        progress = findViewById(R.id.progress);
        btnFinish = findViewById(R.id.btnFinish);
    }

    /**
     * Создает экземпляр нужного анализатора на основе типа теста.
     */
    private void initAnalyzer() {
        switch (type) {
            case MBTI:        analyzer = new MBTIAnalyzer(); break;
            case EQ:          analyzer = new EQAnalyzer(); break;
            case DARK3:       analyzer = new DarkTriadAnalyzer(); break;
            case BIG5_PART1:  analyzer = new PersonalityAnalyzer.BigFivePart1Analyzer(); break;
            case BIG5_PART2:  analyzer = new PersonalityAnalyzer.BigFivePart2Analyzer(); break;
            case VARK:        analyzer = new VarkAnalyzer(); break;
            case BIG5:        analyzer = new PersonalityAnalyzer(); break;
        }
    }

    /**
     * Обновляет UI прогресса прохождения теста.
     * @param answered Количество отвеченных вопросов.
     */
    private void updateProgress(int answered) {
        tvProgress.setText(answered + "/" + total);
        if (total > 0) {
            // Расчет процента заполнения для ProgressBar
            int p = (int) (100 * (double) answered / total);
            progress.setProgress(p);
        }
    }

    /**
     * Логика при нажатии кнопки "Готово". Проверяет полноту ответов.
     */
    private void finishTest() {
        List<Integer> unanswered = new ArrayList<>();
        // Собираем индексы вопросов, на которые нет ответа
        for (int i = 0; i < questions.size(); i++) {
            if (adapter.answers == null || i >= adapter.answers.length || adapter.answers[i] == -1) {
                unanswered.add(i);
            }
        }

        if (unanswered.isEmpty()) {
            // Если всё заполнено, отправляем результат
            submitTestResults();
        } else {
            // Если есть пропуски, показываем диалог-предупреждение
            showUnansweredDialog(unanswered);
        }
    }

    /**
     * Показывает диалог, если пользователь ответил не на все вопросы.
     */
    private void showUnansweredDialog(List<Integer> unanswered) {
        new AlertDialog.Builder(this)
                .setTitle("Пропущены вопросы")
                .setMessage("Пожалуйста, ответьте на все вопросы (" + unanswered.size() + " осталось).")

                // Вариант 1: Подсветить пропущенные вопросы красным и прокрутить к первому
                .setPositiveButton("Подсветить", (d, w) -> {
                    for (int i : unanswered) {
                        questions.get(i).showHighlight = true;
                    }
                    adapter.notifyDataSetChanged();
                    recycler.smoothScrollToPosition(unanswered.get(0));
                })

                // Вариант 2: Автоматически проставить "Нейтрально" для пропущенных и закончить
                .setNeutralButton("Заполнить нейтрально и сдать", (d, w) -> {
                    for (int i : unanswered) {
                        if (adapter.answers != null && i < adapter.answers.length) {
                            adapter.answers[i] = 2; // Код нейтрального ответа
                        }
                        questions.get(i).selectedValue = 2;
                        questions.get(i).isTouched = true;
                    }
                    adapter.notifyDataSetChanged();
                    submitTestResults();
                })

                .setNegativeButton("Отмена", null)
                .show();
    }

    /**
     * Выполняет финальные расчеты и переходит к экрану результатов.
     */
    private void submitTestResults() {
        btnFinish.setEnabled(false); // Отключаем кнопку для защиты от повторных кликов
        recycler.animate().alpha(0f).setDuration(400).start(); // Плавное исчезновение списка

        // Задержка перед переходом для визуального эффекта
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("type", type.name());

            int[] rawScores = calculateRaw(); // Расчет баллов по шкалам
            analyzer.packIntent(intent, rawScores); // Упаковка данных анализатором

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Закрываем экран теста
        }, 600);

        // Начисление XP за прохождение теста через XpManager
        XpManager.addXp(XpManager.XP_REWARD_TEST, (newXp, leveledUp) -> {});
    }

    /**
     * Рассчитывает суммарные баллы по каждой психологической шкале.
     * @return Массив баллов.
     */
    private int[] calculateRaw() {
        int[] scores = new int[5]; // Предполагаем максимум 5 шкал
        for (int i = 0; i < questions.size(); i++) {
            int val = 2; // Дефолт (нейтрально)
            if (adapter.answers != null && i < adapter.answers.length && adapter.answers[i] != -1) {
                val = adapter.answers[i];
            }

            // Если вопрос реверсивный, инвертируем балл (4 становится 0, 3 становится 1 и т.д.)
            if (questions.get(i).isReverse()) {
                val = 4 - val;
            }

            // Добавляем балл к соответствующей шкале (trait)
            scores[questions.get(i).getTrait()] += val;
        }
        return scores;
    }
}
