package vahagn.zargaryan.mindtype;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private TextView tvDesc, tvType;
    private Button btnExit, btnRetry;
    private SpiderChartView spiderChart;
    private BaseAnalyzer analyzer;
    private TestType currentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 1. Инициализация всех View
        initViews();

        // 2. Получение типа теста
        String typeExtra = getIntent().getStringExtra("type");
        if (typeExtra == null) {
            finish();
            return;
        }

        try {
            currentType = TestType.valueOf(typeExtra);
        } catch (IllegalArgumentException e) {
            finish();
            return;
        }

        // 3. Выбор анализатора
        setupAnalyzer(currentType);

        if (analyzer == null) {
            tvDesc.setText("Ошибка: анализатор не найден.");
            return;
        }

        // 4. Расчет и вывод результатов
        displayResults();

        // Безопасно вытаскиваем результат теста
        String finalScore = getIntent().getStringExtra("resultScore");
        if (finalScore == null || finalScore.isEmpty()) {
            // Если для MBTI "resultScore" пустой, берем сгенерированный код типа (например, INTJ)
            finalScore = analyzer.getTitle(getIntent());
        }

        saveAndUnlock(typeExtra, finalScore);

        // 5. Анимации и слушатели
        runCircularReveal();
        setupClickListeners();
    }

    private void initViews() {
        tvDesc = findViewById(R.id.tvDesc);
        tvType = findViewById(R.id.tvType);
        btnExit = findViewById(R.id.btnExit);
        btnRetry = findViewById(R.id.btnRetry);
        spiderChart = findViewById(R.id.chartContainer);
    }

    private void setupAnalyzer(TestType type) {
        switch (type) {
            case MBTI:        analyzer = new MBTIAnalyzer(); break;
            case EQ:          analyzer = new EQAnalyzer(); break;
            case DARK3:       analyzer = new DarkTriadAnalyzer(); break;
            case BIG5:        analyzer = new PersonalityAnalyzer(); break;
            case VARK:        analyzer = new VarkAnalyzer(); break;
        }
    }

    private void displayResults() {
        try {
            // Устанавливаем текст анализа
            tvDesc.setText(analyzer.getAnalysis(getIntent()));

            // Устанавливаем заголовок (тип MBTI или название теста)
            String title = analyzer.getTitle(getIntent());
            tvType.setText(title != null && !title.isEmpty() ? title : currentType.name());

            // Заполняем график
            Map<String, Integer> chartData = analyzer.getChartData(getIntent());
            if (chartData != null && spiderChart != null) {
                spiderChart.setData(chartData);
            }

            // Автоматическое сохранение графиков в Firebase (если реализовано в анализаторе)
            analyzer.saveResultsToFirebase(chartData);

        } catch (Exception e) {
            e.printStackTrace();
            tvDesc.setText("Произошла ошибка при обработке данных результатов.");
        }
    }

    private void setupClickListeners() {
        if (btnExit != null) {
            btnExit.setOnClickListener(v -> {
                // Убедись, что класс StartActivity или MainActivity (смотря что у тебя главное) называется именно так
                startActivity(new Intent(this, StartActivity.class));
                finish();
            });
        }

        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("type", currentType.name());
                startActivity(i);
                finish();
            });
        }
    }

    private void runCircularReveal() {
        final View rootView = findViewById(R.id.resultRoot);
        if (rootView == null) return;

        rootView.post(() -> {
            int cx = rootView.getWidth() / 2;
            int cy = rootView.getHeight() / 2;
            float finalRadius = (float) Math.hypot(cx, cy);

            try {
                Animator anim = ViewAnimationUtils.createCircularReveal(rootView, cx, cy, 0, finalRadius);
                anim.setDuration(800);
                rootView.setVisibility(View.VISIBLE);
                anim.start();
            } catch (Exception e) {
                rootView.setVisibility(View.VISIBLE); // Фолбек, если анимация не поддерживается или сбоит
            }
        });
    }

    private void saveAndUnlock(String testType, String resultScore) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid);
        Map<String, Object> updates = new HashMap<>();

        String normalizedType = testType.toUpperCase();

        // Защита: если даже после всех проверок пришел null, не даем упасть базе данных
        String safeScore = (resultScore != null) ? resultScore : "Пройден";

        if (normalizedType.equals("MBTI")) {
            updates.put("mbtiType", safeScore);
            updates.put("mbtiDone", true);
        } else {
            updates.put(normalizedType.toLowerCase() + "Score", safeScore);
        }

        ref.updateChildren(updates).addOnSuccessListener(aVoid -> {
            String taskId = findTaskIdByTestType(normalizedType);

            if (taskId != null) {
                DailyTasksManager.completeTask(taskId, (reward, leveledUp) -> {
                    Toast.makeText(getApplicationContext(), "Задача выполнена! +" + reward + " XP", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private String findTaskIdByTestType(String testType) {
        if (testType == null) return null;
        for (TaskType type : TaskType.values()) {
            if (type.testType != null && type.testType.equalsIgnoreCase(testType)) {
                return type.id;
            }
        }
        return null;
    }
}