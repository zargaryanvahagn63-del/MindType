package vahagn.zargaryan.mindtype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vahagn.zargaryan.mindtype.alarm.BatteryOptimizationHelper;
import vahagn.zargaryan.mindtype.alarm.MoodAlarmScheduler;
import vahagn.zargaryan.mindtype.analyzers.MBTIAnalyzer;
import vahagn.zargaryan.mindtype.journal.JournalActivity;
import vahagn.zargaryan.mindtype.result.ResultDetailDialog;
import vahagn.zargaryan.mindtype.result.ResultsAdapter;
import vahagn.zargaryan.mindtype.result.TestResult;

/**
 * Фрагмент профиля пользователя для экосистемы MindType.
 * Отображает статистику пользователя, RPG-ранг, шкалу опыта, MBTI-тип,
 * архив результатов тестирования, а также предоставляет гибкие настройки
 * уведомлений и обхода системных ограничений энергопотребления (АКБ).
 */
public class ProfileFragment extends Fragment {

    // Основные элементы интерфейса профиля
    private TextView tvUsername, tvRank, tvXp, tvTestsCount, tvMbtiCode, tvMbtiName, tvMbtiDesc;
    private ProgressBar xpProgressBar;
    private View avatarCircle;
    private MaterialButton btnLogout;

    // НОВАЯ ПЕРЕМЕННАЯ: Кнопка перехода в дневник
    private View btnOpenJournal;

    // Компоненты панели настроек уведомлений и работы в фоне
    private com.google.android.material.materialswitch.MaterialSwitch switchNotifications;
    private MaterialButton btnBatterySettings;

    // Компоненты для интерактивного списка архивных результатов
    private androidx.recyclerview.widget.RecyclerView rvTestResults;
    private ResultsAdapter resultsAdapter;

    // Обработчики и аналитика
    private MBTIAnalyzer mbtiAnalyzer;

    // Ссылки на базу данных Firebase
    private DatabaseReference userRef;
    private DatabaseReference resRef;

    // Слушатели базы данных
    private ValueEventListener userListener;
    private ValueEventListener resultsListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle b) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mbtiAnalyzer = new MBTIAnalyzer();

        initViews(view);
        setupSettingsLogic();
        setupClickListeners();
        loadUserData();
        loadSavedResults();

        return view;
    }

    private void initViews(View v) {
        tvUsername = v.findViewById(R.id.tv_username);
        tvRank = v.findViewById(R.id.tv_rank);
        tvXp = v.findViewById(R.id.tv_xp);
        tvTestsCount = v.findViewById(R.id.tv_tests_count);
        tvMbtiCode = v.findViewById(R.id.tv_mbti_code);
        tvMbtiName = v.findViewById(R.id.tv_mbti_name);
        tvMbtiDesc = v.findViewById(R.id.tv_mbti_desc);
        xpProgressBar = v.findViewById(R.id.xpProgressBar);
        avatarCircle = v.findViewById(R.id.avatarCircle);
        btnLogout = v.findViewById(R.id.btn_logout);

        // Связываем новую кнопку в разметке
        btnOpenJournal = v.findViewById(R.id.btnOpenJournal);

        switchNotifications = v.findViewById(R.id.switchNotifications);
        btnBatterySettings = v.findViewById(R.id.btnBatteryOptimize);

        rvTestResults = v.findViewById(R.id.rvTestResults);
        resultsAdapter = new ResultsAdapter();
        if (rvTestResults != null) {
            rvTestResults.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
            rvTestResults.setAdapter(resultsAdapter);
        }
    }

    private void setupSettingsLogic() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        boolean isNotifEnabled = prefs.getBoolean("notifications_on", false);
        switchNotifications.setChecked(isNotifEnabled);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_on", isChecked).apply();
            if (isChecked) {
                checkNotificationPermissionsAndStart();
            } else {
                Toast.makeText(getContext(), "Напоминания приостановлены", Toast.LENGTH_SHORT).show();
            }
        });

        btnBatterySettings.setOnClickListener(v -> {
            if (getContext() != null) {
                BatteryOptimizationHelper.requestIgnoreBatteryOptimization(getContext());
                BatteryOptimizationHelper.openAutostartSettings(getContext());
            }
        });
    }

    private void checkNotificationPermissionsAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        MoodAlarmScheduler.scheduleAllAlarms(getContext());
        Toast.makeText(getContext(), "Напоминания активны: 9:00, 14:00, 20:00", Toast.LENGTH_SHORT).show();
    }

    /**
     * ИСПРАВЛЕННЫЙ МЕТОД:
     * Очищает стек экранов, делает выход из учетной записи Firebase
     * и гарантированно перенаправляет на экран регистрации/авторизации.
     * Также обрабатывает переход на экран дневника.
     */
    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> {
            if (getActivity() == null) return;

            // 1. Разлогиниваемся в Firebase Auth
            FirebaseAuth.getInstance().signOut();

            // 2. Направляем пользователя на активность авторизации
            Intent intent = new Intent(getActivity(), LoginActivity.class);

            // Флаги CLEAR_TASK и NEW_TASK полностью стирают историю переходов назад.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            getActivity().finish();
        });

        // Слушатель для открытия дневника по кнопке в профиле
        if (btnOpenJournal != null) {
            btnOpenJournal.setOnClickListener(v -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), JournalActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) updateUI(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Ошибка базы данных профиля: " + error.getMessage());
            }
        });
    }

    private void loadSavedResults() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        resRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("results");
        resultsListener = resRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                List<TestResult> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    TestResult res = ds.getValue(TestResult.class);
                    if (res != null) list.add(res);
                }
                resultsAdapter.setData(list, result -> {
                    ResultDetailDialog dialog = ResultDetailDialog.newInstance(result);
                    dialog.show(getChildFragmentManager(), "result_detail");
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Ошибка загрузки истории тестов: " + error.getMessage());
            }
        });
    }

    private void updateUI(User user) {
        tvUsername.setText(user.username);
        tvTestsCount.setText("Тестов: " + user.testsCount);

        int currentXp = user.xp;
        int nextLevelXp = 500;
        tvXp.setText(currentXp + " / " + nextLevelXp + " XP");
        xpProgressBar.setMax(nextLevelXp);
        xpProgressBar.setProgress(currentXp);

        tvRank.setText("Ранг: " + calculateRank(currentXp));

        if (user.mbtiType != null && !user.mbtiType.isEmpty()) {
            tvMbtiCode.setText(user.mbtiType);
            tvMbtiName.setText(mbtiAnalyzer.getTypeName(user.mbtiType));
            tvMbtiDesc.setText(mbtiAnalyzer.getDetailedSummary(user.mbtiType));
        }

        avatarCircle.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#33BB86FC")));
    }

    private String calculateRank(int xp) {
        if (xp < 1000) return "Новичок";
        if (xp < 3000) return "Исследователь";
        if (xp < 6000) return "Аналитик";
        if (xp < 10000) return "Мастер разума";
        return "Гуру";
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
        if (resRef != null && resultsListener != null) {
            resRef.removeEventListener(resultsListener);
        }
    }
}