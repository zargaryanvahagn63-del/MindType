package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Splash-экран приложения.
 * Отвечает за отображение логотипа при запуске и логику перенаправления пользователя
 * (в туториал, на экран логина или сразу в главное меню).
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Включение отображения контента под системными панелями (Edge-to-Edge)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Настройка отступов для корректного отображения на экранах с вырезами
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logo = findViewById(R.id.logo);
        TextView text = findViewById(R.id.appName);

        // Анимация появления логотипа и названия
        logo.animate()
                .alpha(1f)
                .setDuration(1000)
                .start();

        text.animate()
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(1000)
                .start();

        // Анимация масштабирования логотипа
        logo.setScaleX(0.5f);
        logo.setScaleY(0.5f);
        logo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(1000)
                .start();

        // Отложенный переход на следующий экран (через 2.5 секунды)
        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("MindTypePrefs", MODE_PRIVATE);
            boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

            // Проверяем, авторизован ли пользователь в Firebase
            com.google.firebase.auth.FirebaseUser currentUser =
                    com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

            Intent intent;
            if (isFirstRun) {
                // Если первый запуск — показываем обучающий экран
                intent = new Intent(SplashActivity.this, TutorialActivity.class);
            } else if (currentUser == null) {
                // Если не авторизован — на экран входа
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            } else {
                // Если авторизован — в основную часть приложения
                intent = new Intent(SplashActivity.this, StartActivity.class);
            }

            startActivity(intent);
            finish(); // Удаляем Splash из стека активностей
        }, 2500);
    }
}
