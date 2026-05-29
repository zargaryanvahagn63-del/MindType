package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Активность входа в приложение.
 * Позволяет пользователю авторизоваться через Firebase Auth,
 * перейти к регистрации или восстановить пароль.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword; // Поля ввода почты и пароля
    private MaterialButton btnLogin;              // Кнопка входа
    private TextView tvForgotPassword, tvGoToRegister; // Текстовые ссылки
    private FirebaseAuth auth;                     // Объект Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Привязка элементов разметки
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Настройка обработчиков нажатий
        btnLogin.setOnClickListener(v -> loginUser());
        
        // Переход на экран регистрации
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        
        // Открытие диалога восстановления пароля
        tvForgotPassword.setOnClickListener(v -> showRecoverPasswordDialog());
    }

    /**
     * Выполняет попытку входа пользователя.
     */
    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Проверка на заполнение полей
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Блокируем кнопку во время запроса
        btnLogin.setEnabled(false);

        // Авторизация через Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        // При успешном входе переходим на стартовый экран
                        Intent intent = new Intent(LoginActivity.this, StartActivity.class);
                        // Очищаем стек активностей, чтобы нельзя было вернуться назад к логину
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Ошибка входа: проверьте данные", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Показывает диалоговое окно для сброса пароля через Email.
     */
    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Восстановление пароля");

        final EditText input = new EditText(this);
        input.setHint("Введите ваш Email");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Отправить", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                // Запрос Firebase на отправку письма для сброса пароля
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Письмо для сброса отправлено", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Ошибка отправки", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
