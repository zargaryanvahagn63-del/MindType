package vahagn.zargaryan.mindtype.result;

import vahagn.zargaryan.mindtype.alarm.CooldownNotificationHelper;
import vahagn.zargaryan.mindtype.analyzers.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;

import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.SpiderChartView;
import vahagn.zargaryan.mindtype.tests.TestType;

/**
 * Исправленная и оптимальная активность для отображения результатов теста.
 * Реализует безопасное слияние частей Большой Пятерки и гарантирует
 * вызов методов сохранения специфических результатов для предотвращения ошибок графиков.
 */
public class ResultActivity extends AppCompatActivity {

    private TextView tvDesc, tvType;
    private Button btnExit;
    private SpiderChartView spiderChart;
    private BaseAnalyzer analyzer;
    private TestType currentType;

    // Длительность ограничения между тестами
    private static final long COOLDOWN_MS = (long) (0.5 * 60L * 60L * 1000L);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initViews();
        checkAndLoadData();

        btnExit.setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvDesc = findViewById(R.id.tvDesc);
        tvType = findViewById(R.id.tvType);
        btnExit = findViewById(R.id.btnExit);
        spiderChart = findViewById(R.id.chartContainer);
    }

    /**
     * Контролирует поток выполнения. Если это вторая часть Большой Пятерки,
     * асинхронно скачивает из базы данных результаты первой части.
     */
    private void checkAndLoadData() {
        String typeStr = getIntent().getStringExtra("type");
        if (typeStr == null) { finish(); return; }
        currentType = TestType.valueOf(typeStr);

        setupAnalyzer();

        if (currentType == TestType.BIG5_PART2) {
            mergeBigFivePart1AndProceed();
        } else {
            proceedWithUIAndHistory();
        }
    }

    /**
     * Загружает из Firebase проценты первой части Big Five и добавляет их в Intent.
     */
    private void mergeBigFivePart1AndProceed() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            proceedWithUIAndHistory();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("personalityResults");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int e = getSafeIntValue(snapshot.child("Экстраверсия"));
                    int a = getSafeIntValue(snapshot.child("Дружелюбие"));
                    int c = getSafeIntValue(snapshot.child("Сознательность"));

                    getIntent().putExtra("E", e);
                    getIntent().putExtra("A", a);
                    getIntent().putExtra("C", c);
                }
                proceedWithUIAndHistory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                proceedWithUIAndHistory();
            }
        });
    }

    /**
     * Предотвращает ClassCastException при чтении чисел из Firebase,
     * которые могут приходить как Long вместо Integer.
     */
    private int getSafeIntValue(DataSnapshot childSnapshot) {
        if (childSnapshot.exists() && childSnapshot.getValue() != null) {
            Object val = childSnapshot.getValue();
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
        }
        return 0;
    }

    private void proceedWithUIAndHistory() {
        setupData();
        processProgressUpdate();
    }

    /**
     * Заполняет текстовые поля анализа и передает данные на паутинную диаграмму.
     */
    private void setupData() {
        if (currentType == TestType.BIG5_PART1) {
            tvType.setText("Часть 1 завершена");
            tvDesc.setText("Вы успешно прошли первую часть. Пройдите VARK, чтобы открыть финальную часть исследования личности.");
            if (spiderChart != null) spiderChart.setVisibility(View.GONE);
        } else {
            if (spiderChart != null) spiderChart.setVisibility(View.VISIBLE);
            tvDesc.setText(analyzer.getAnalysis(getIntent()));
            tvType.setText(analyzer.getTitle(getIntent()));

            Map<String, Integer> data = analyzer.getChartData(getIntent());
            if (spiderChart != null && data != null) {
                spiderChart.setData(data);
            }
        }
    }

    /**
     * Повышает шаг прогресса пользователя в базе данных и устанавливает
     * временную метку блокировки (кулдауна).
     */
    private void processProgressUpdate() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid);

        ref.child("currentStep").get().addOnSuccessListener(snapshot -> {
            int step = 0;
            if (snapshot.exists() && snapshot.getValue() != null) {
                step = ((Number) snapshot.getValue()).intValue();
            }

            if (currentType.isCurrent(step)) {
                ref.child("currentStep").setValue(step + 1);

                // Записываем время прохождения теста для активации кулдауна
                ref.child("lastTestTimestamp").setValue(System.currentTimeMillis());

                // Запускаем тихое неудаляемое уведомление с таймером обратного отсчета в шторке
                CooldownNotificationHelper.startCooldownNotification(this, COOLDOWN_MS);
            }
            saveHistoryResult(ref);
        });
    }

    /**
     * Сохраняет результаты теста в архив по детерминированному ключу (заменяя старую попытку).
     * Если пройдена Часть 2, бесследно удаляет Часть 1 из истории.
     */
    private void saveHistoryResult(DatabaseReference ref) {
        if (analyzer == null) return;

        String name = analyzer.getTitle(getIntent());
        String headline = analyzer.getMainResult(getIntent());
        String fullAnalysis = analyzer.getAnalysis(getIntent());
        Map<String, Integer> chart = analyzer.getChartData(getIntent());

        TestResult historyRes = new TestResult(name, headline, fullAnalysis, chart);

        // Перезаписываем старое прохождение данного теста новым результатом (без .push())
        ref.child("results").child(currentType.name()).setValue(historyRes);

        // Если это Часть 2 — точечно стираем Часть 1 из истории, чтобы не засорять профиль
        if (currentType == TestType.BIG5_PART2) {
            ref.child("results").child(TestType.BIG5_PART1.name()).removeValue();
        }

        // Сохраняем шкалы в специфическую ветку (например, personalityResults для Big Five)
        analyzer.saveResultsToFirebase(chart);

        // Совместимость для главного профиля
        if (currentType == TestType.MBTI) {
            String mbti = getIntent().getStringExtra("MBTI_TYPE");
            if (mbti != null) ref.child("mbtiType").setValue(mbti);
            ref.child("mbtiDone").setValue(true);
        }
    }

    private void setupAnalyzer() {
        switch (currentType) {
            case MBTI: analyzer = new MBTIAnalyzer(); break;
            case EQ: analyzer = new EQAnalyzer(); break;
            case DARK3: analyzer = new DarkTriadAnalyzer(); break;
            case BIG5_PART1: analyzer = new PersonalityAnalyzer.BigFivePart1Analyzer(); break;
            case BIG5_PART2: analyzer = new PersonalityAnalyzer.BigFivePart2Analyzer(); break;
            case VARK: analyzer = new VarkAnalyzer(); break;
            case BIG5: analyzer = new PersonalityAnalyzer(); break;
        }
    }
}