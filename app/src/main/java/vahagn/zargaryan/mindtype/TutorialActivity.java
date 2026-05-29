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

/**
 * Активность обучающего экрана (туториала).
 * Знакомит пользователя с механикой тестов (использование SeekBar) и плавно переводит к регистрации.
 */
public class TutorialActivity extends AppCompatActivity {

    private TextView tvTitle, tvDesc, tvInstruction, tvSeekBarInstruction;
    private LinearLayout sliderContainer;
    private Button btnStart;
    private SeekBar demoSeekBar;

    private int currentStage = 0; // Текущий шаг анимации ползунка
    private final Handler loopHandler = new Handler();
    private Runnable sliderRunnable;

    /**
     * Запускает бесконечную анимацию демонстрационного ползунка.
     * Показывает пользователю, как меняются подписи при движении ползунка.
     */
    private void startInfiniteSlider() {
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                // Устанавливаем прогресс программно (с анимацией)
                demoSeekBar.setProgress(currentStage, true);

                // Обновляем текстовую подпись под демо-ползунком
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

                // Переход к следующему значению
                currentStage++;
                if (currentStage > 4) {
                    currentStage = 0; // Цикл
                }

                // Повтор через 1.5 секунды
                loopHandler.postDelayed(this, 1500);
            }
        };

        // Первый запуск
        loopHandler.post(sliderRunnable);
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tutorial);

        // Инициализация View
        tvTitle = findViewById(R.id.tvTutTitle);
        tvDesc = findViewById(R.id.tvTutDesc);
        tvInstruction = findViewById(R.id.tvTutInstruction);
        sliderContainer = findViewById(R.id.sliderContainer);
        btnStart = findViewById(R.id.btnTutStart);
        demoSeekBar = findViewById(R.id.demoSeekBar);
        tvSeekBarInstruction = findViewById(R.id.tvSeekBarInstruction);

        // Запрещаем пользователю двигать демо-ползунок вручную
        demoSeekBar.setOnTouchListener((v, event) -> true);
        demoSeekBar.setProgress(0);

        // Подготовка к каскадной анимации появления элементов
        View[] viewsToAnimate = {tvTitle, tvDesc, tvInstruction, tvSeekBarInstruction, sliderContainer, btnStart};
        for (View v : viewsToAnimate) {
            v.setAlpha(0f);
            v.setTranslationY(50f);
        }

        // Запуск последовательной анимации выплывания (интервал 500мс)
        long delay = 300;
        for (View v : viewsToAnimate) {
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(delay)
                    .start();
            delay += 500;
        }

        // Запуск демонстрации SeekBar
        startInfiniteSlider();

        // Кнопка перехода к приложению
        btnStart.setOnClickListener(v -> {
            // Остановка фонового процесса анимации
            if (loopHandler != null && sliderRunnable != null) {
                loopHandler.removeCallbacks(sliderRunnable);
            }
            finishTutorial();
        });
    }

    /**
     * Завершает туториал, сохраняет флаг прохождения и анимирует выход.
     */
    private void finishTutorial() {
        // Помечаем, что туториал больше не нужно показывать при следующем запуске
        SharedPreferences prefs = getSharedPreferences("MindTypePrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("isFirstRun", false).apply();

        // Анимация разлета элементов перед закрытием
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

        // Переход на экран логина после завершения анимации
        tvTitle.postDelayed(() -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, delay + 200);
    }
}
