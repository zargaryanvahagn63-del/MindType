package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_start);

        findViewById(R.id.btnBigFive).setOnClickListener(v -> open("BIG5"));
        findViewById(R.id.btnMBTI).setOnClickListener(v -> open("MBTI"));
        findViewById(R.id.btnEQ).setOnClickListener(v -> open("EQ"));
        findViewById(R.id.btnDarkTriad).setOnClickListener(v -> open("DARK3"));


        // Находим твои кнопки/карточки выбора тестов
        View cardMBTI = findViewById(R.id.cardMBTI);
        View cardEQ = findViewById(R.id.cardEQ);
        View cardDark3 = findViewById(R.id.cardDark3);
        View cardBig5 = findViewById(R.id.cardBig5);

        View[] cards = {cardMBTI, cardEQ, cardDark3, cardBig5};

        for (View v : cards) {
            v.setAlpha(0f);
            v.setScaleX(0.8f); // Начинаем чуть меньше
            v.setScaleY(0.8f);
        }

// Анимируем с "отскоком"
        long delay = 200;
        for (View v : cards) {
            v.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay(delay)
                    // OvershootInterpolator дает тот самый приятный "пружинистый" эффект
                    .setInterpolator(new android.view.animation.OvershootInterpolator())
                    .start();
            delay += 150;
        }
    }

    void open(String type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);

// Плавный переход: вход снизу + легкое затухание меню
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
    }
}