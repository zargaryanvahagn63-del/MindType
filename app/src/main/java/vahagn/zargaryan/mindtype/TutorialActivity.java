package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    TextView tvTitle, tvDesc, tvInstruction, tvSeekBarInstruction;
    LinearLayout sliderContainer;
    Button btnStart;
    SeekBar demoSeekBar;



    private int currentStage = 0;
    private Handler loopHandler = new Handler();
    private Runnable sliderRunnable;

    private void startInfiniteSlider() {
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                // Двигаем ползунок
                demoSeekBar.setProgress(currentStage, true);

                if(currentStage == 0) {
                    tvSeekBarInstruction.setText("Совсем нет");
                } else if(currentStage == 1) {
                    tvSeekBarInstruction.setText("Скорее нет");
                } else if(currentStage == 2) {
                    tvSeekBarInstruction.setText("Нейтрально");
                } else if(currentStage == 3) {
                    tvSeekBarInstruction.setText("Скорее да");
                } else if(currentStage == 4) {
                    tvSeekBarInstruction.setText("Полностью!");
                }

                // Логика перехода к следующему этапу
                currentStage++;
                if (currentStage > 4) {
                    currentStage = 0; // Сбрасываем в начало
                }

                // "Зацикливаем" — вызываем этот же код через 1.5 секунды
                loopHandler.postDelayed(this, 1500);
            }
        };

        // Запускаем первый раз
        loopHandler.post(sliderRunnable);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tutorial);

        tvTitle = findViewById(R.id.tvTutTitle);
        tvDesc = findViewById(R.id.tvTutDesc);
        tvInstruction = findViewById(R.id.tvTutInstruction);
        sliderContainer = findViewById(R.id.sliderContainer);
        btnStart = findViewById(R.id.btnTutStart);
        demoSeekBar = findViewById(R.id.demoSeekBar);
        tvSeekBarInstruction = findViewById(R.id.tvSeekBarInstruction);

        demoSeekBar.setOnTouchListener((v, event) -> true);
        demoSeekBar.setProgress(0);

        // Подготавливаем элементы к анимации (прячем и сдвигаем вниз на 50 пикселей)
        View[] viewsToAnimate = {tvTitle, tvDesc, tvInstruction, tvSeekBarInstruction, sliderContainer, btnStart};
        for (View v : viewsToAnimate) {
            v.setAlpha(0f);
            v.setTranslationY(50f);
        }

        // Запускаем каскадную анимацию
        long delay = 300; // Начальная задержка
        for (View v : viewsToAnimate) {
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(delay)
                    .start();
            delay += 500; // Каждый следующий элемент выплывает на 500мс позже
        }

        startInfiniteSlider();

        btnStart.setOnClickListener(v -> {
            // Останавливаем бесконечный цикл перед уходом
            if (loopHandler != null && sliderRunnable != null) {
                loopHandler.removeCallbacks(sliderRunnable);
            }
            finishTutorial();
        });
    }

    private void finishTutorial() {
        // Сохраняем флаг
        SharedPreferences prefs = getSharedPreferences("MindTypePrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("isFirstRun", false).apply();

        // Анимация исчезновения (в обратном порядке)
        View[] viewsToAnimate = {btnStart, tvSeekBarInstruction, sliderContainer, tvInstruction, tvDesc, tvTitle};
        long delay = 0;

        for (View v : viewsToAnimate) {
            v.animate()
                    .alpha(0f)
                    .translationY(-100f) // Улетают вверх
                    .setDuration(400)
                    .setStartDelay(delay)
                    .start();
            delay += 100;
        }

        // Запускаем переход после того, как последний элемент (заголовок) начнет исчезать
        tvTitle.postDelayed(() -> {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);

            // Плавный переход между Activity (Zoom + Fade)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            finish();
        }, delay + 200);
    }
}