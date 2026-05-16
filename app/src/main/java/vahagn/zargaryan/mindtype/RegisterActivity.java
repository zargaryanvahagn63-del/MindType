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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etPasswordConfirm;
    private MaterialButton btnRegister;
    private TextView tvGoToLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> registerUser());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    btnRegister.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Выносим создание записи в базе в отдельную функцию
                            createNewUserRecord(firebaseUser);

                            // Сразу отправляем письмо для подтверждения
                            firebaseUser.sendEmailVerification();

                            Toast.makeText(this, "Регистрация успешна! Подтвердите Email", Toast.LENGTH_LONG).show();

                            // Переходим на экран входа
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка";
                        Toast.makeText(this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNewUserRecord(FirebaseUser firebaseUser) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Безопасно извлекаем имя из почты
        String email = firebaseUser.getEmail();
        String defaultName = (email != null) ? email.split("@")[0] : "User";

        // Создаем объект нашей модели (которую мы создали ранее)
        User newUser = new User(defaultName, 0, 0, "Не определен", null, null);

        // Сохраняем по UID пользователя
        mDatabase.child(firebaseUser.getUid()).setValue(newUser)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Профиль успешно создан в БД"))
                .addOnFailureListener(e -> Log.e("Firebase", "Ошибка записи профиля: " + e.getMessage()));
    }
}