package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Активность регистрации нового пользователя.
 * Позволяет создать учетную запись через Firebase Auth и инициализировать профиль в базе данных.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etPasswordConfirm; // Поля ввода
    private MaterialButton btnRegister; // Кнопка регистрации
    private TextView tvGoToLogin;       // Ссылка на экран входа
    private FirebaseAuth auth;          // Объект Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Инициализация сервиса авторизации
        auth = FirebaseAuth.getInstance();

        // Привязка UI-элементов
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Обработка клика по кнопке регистрации
        btnRegister.setOnClickListener(v -> registerUser());
        
        // Обработка перехода на экран входа
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Валидация данных и создание пользователя в Firebase.
     */
    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();

        // Проверка пустых полей
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка совпадения паролей
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка длины пароля (требование Firebase)
        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        // Блокируем кнопку, чтобы избежать повторных нажатий
        btnRegister.setEnabled(false);

        // Создание аккаунта в Firebase
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    btnRegister.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Создаем запись в Realtime Database
                            createNewUserRecord(firebaseUser);

                            // Отправка письма для подтверждения почты
                            firebaseUser.sendEmailVerification();

                            Toast.makeText(this, "Регистрация успешна! Подтвердите Email", Toast.LENGTH_LONG).show();

                            // После регистрации отправляем пользователя на вход
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        // Обработка ошибок регистрации (например, Email уже занят)
                        String error = task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка";
                        Toast.makeText(this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Создает начальный профиль пользователя в базе данных Firebase.
     * @param firebaseUser Авторизованный объект пользователя.
     */
    private void createNewUserRecord(FirebaseUser firebaseUser) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Генерация дефолтного имени пользователя из части Email
        String email = firebaseUser.getEmail();
        String defaultName = (email != null) ? email.split("@")[0] : "User";

        // Создание объекта модели User с начальными данными
        User newUser = new User(defaultName, 0, 0, "Не определен", null, null);

        // Сохранение в узел по UID пользователя
        mDatabase.child(firebaseUser.getUid()).setValue(newUser)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Профиль успешно создан в БД"))
                .addOnFailureListener(e -> Log.e("Firebase", "Ошибка записи профиля: " + e.getMessage()));
    }
}
