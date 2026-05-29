package vahagn.zargaryan.mindtype;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import vahagn.zargaryan.mindtype.alarm.MoodAlarmScheduler;
import vahagn.zargaryan.mindtype.leaderboard.LeaderboardFragment;
import vahagn.zargaryan.mindtype.tasks.TasksFragment;
import vahagn.zargaryan.mindtype.tests.HomeFragment;

/**
 * Класс контейнера навигации StartActivity.
 * Выполняет роль умного роутера на входе: если сессия пользователя пуста,
 * превентивно перенаправляет его на экран авторизации.
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // КРИТИЧЕСКИ ВАЖНЫЙ ФИКС:
        // Проверяем, вошел ли пользователь в систему.
        // Если FirebaseAuth не находит активного токена (например, после логаута),
        // мы прерываем загрузку интерфейса и выкидываем на экран авторизации.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Замени LoginActivity.class на имя своего класса входа/регистрации (например, AuthActivity.class)
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Закрываем StartActivity, чтобы в нее нельзя было вернуться кнопкой "Назад"
            return;
        }

        // Если пользователь авторизован, штатно инициализируем интерфейс навигации
        setContentView(R.layout.activity_start);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_leaderboard) {
                selectedFragment = new LeaderboardFragment();
            } else if (itemId == R.id.nav_tasks) {
                selectedFragment = new TasksFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}