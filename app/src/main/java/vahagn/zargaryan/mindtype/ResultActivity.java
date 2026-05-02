package vahagn.zargaryan.mindtype;

import static vahagn.zargaryan.mindtype.TestType.*;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    TextView tvDesc, tvType;
    Button btnExit, btnRetry;
    BaseAnalyzer analyzer;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_result);

        tvDesc = findViewById(R.id.tvDesc);
        tvType = findViewById(R.id.tvType);
        btnExit = findViewById(R.id.btnExit);
        btnRetry = findViewById(R.id.btnRetry);

        runCircularReveal();


        String t = getIntent().getStringExtra("type");
        if (t == null) return;

        TestType type;
        try {
            type = TestType.valueOf(t);
        } catch (Exception e) {
            return;
        }

        switch (type) {
            case MBTI:
                analyzer = new MBTIAnalyzer();
                break;
            case EQ:
                analyzer = new EQAnalyzer();
                break;
            case DARK3:
                analyzer = new DarkTriadAnalyzer();
                break;
            case BIG5:
                analyzer = new PersonalityAnalyzer();
                break;
            default:
                break;
        }

        if (analyzer != null) {
            String resultText = analyzer.getAnalysis(getIntent());
            tvDesc.setText(resultText);
            tvType.setText(analyzer.getTitle(getIntent()));
            if (type == MBTI) {
                // Если тест MBTI, берем код типа из Intent
                String mbtiCode = getIntent().getStringExtra("MBTI_TYPE");
                tvType.setText(mbtiCode != null ? mbtiCode : "MBTI");
            } else {
                // Для остальных просто выводим название теста
                tvType.setText(type.name());
            }
        }

        SpiderChartView spiderChart = findViewById(R.id.chartContainer);
        TextView tvDesc = findViewById(R.id.tvDesc);

        TestType testType = TestType.valueOf(getIntent().getStringExtra("type"));

// 1. Получаем тип теста из Intent (например, "DARK_TRIAD")
        BaseAnalyzer analyzer = null;

// Логика выбора (If-else / Switch)
        if (testType != null) {
            switch (type) {
                case MBTI:
                    analyzer = new MBTIAnalyzer();
                    break;
                case EQ:
                    analyzer = new EQAnalyzer();
                    break;
                case DARK3:
                    analyzer = new DarkTriadAnalyzer();
                    break;
                case BIG5:
                    analyzer = new PersonalityAnalyzer();
                    break;
                default:
                    break;
            }
        }

        if (analyzer == null) {
            // Предсказуемая логическая ошибка: мы не знаем такой тест
            tvDesc.setText("Тест не найден или результаты устарели.");
            return; // Прерываем выполнение
        }

// А вот здесь можно обернуть в try-catch для страховки от математических сбоев внутри анализатора
        try {
            String analysisText = analyzer.getAnalysis(getIntent());
            Map<String, Integer> chartData = analyzer.getChartData(getIntent());

            tvDesc.setText(analysisText);
            spiderChart.setData(chartData);
        } catch (Exception e) {
            // Сюда мы попадем, только если внутри getAnalysis или getChartData произошел крах
            e.printStackTrace(); // Запишем в логи (Logcat), чтобы потом исправить баг
            tvDesc.setText("Произошла ошибка при расчете ваших результатов.");
        }

        // Запускаем красивую анимацию
        findViewById(R.id.resultRoot).setAlpha(0f);
        findViewById(R.id.resultRoot).animate().alpha(1f).setDuration(1000).start();

        btnExit.setOnClickListener(v -> {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        });

        btnRetry.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            i.putExtra("type", type.name());

            startActivity(i);
            finish();
        });
    }

    private void runCircularReveal() {
        final View rootView = findViewById(R.id.resultRoot); // ID твоего корневого Layout
        rootView.post(() -> {
            int cx = rootView.getWidth() / 2;
            int cy = rootView.getHeight() / 2;
            float finalRadius = (float) Math.hypot(cx, cy);

            // Создаем анимацию "открывающегося круга"
            Animator anim = ViewAnimationUtils.createCircularReveal(rootView, cx, cy, 0, finalRadius);
            anim.setDuration(800);
            rootView.setVisibility(View.VISIBLE);
            anim.start();
        });
    }
}