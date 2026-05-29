package vahagn.zargaryan.mindtype.result;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;
import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.alarm.TimeManager;

public class WeeklyReportActivity extends AppCompatActivity {

    private TextView tvSummary;
    private TextInputLayout inputReflection;
    private Button btnSave;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_report);

        initViews();

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            loadAndAnalyzeWeek();
        }

        btnSave.setOnClickListener(v -> saveUserReflection());
    }

    private void initViews() {
        tvSummary = findViewById(R.id.tvWeeklySummary);
        inputReflection = findViewById(R.id.inputReflection);
        btnSave = findViewById(R.id.btnSaveReflection);
    }

    private void loadAndAnalyzeWeek() {
        // Получаем ключи последних 7 дней (yyyy-MM-dd)
        List<String> last7Days = getLastSevenDays();

        userRef.child("mood_history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalScore = 0;
                int entryCount = 0;

                for (String dayKey : last7Days) {
                    if (snapshot.hasChild(dayKey)) {
                        DataSnapshot dayData = snapshot.child(dayKey);
                        // Проверяем слоты: morning, afternoon, evening
                        String[] slots = {"morning", "afternoon", "evening"};
                        for (String slot : slots) {
                            if (dayData.hasChild(slot)) {
                                Integer value = dayData.child(slot).getValue(Integer.class);
                                if (value != null) {
                                    totalScore += value;
                                    entryCount++;
                                }
                            }
                        }
                    }
                }

                generateSummary(entryCount > 0 ? (float) totalScore / entryCount : -1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void generateSummary(float avgMood) {
        if (avgMood == -1) {
            tvSummary.setText("На этой неделе ты заходил редко. Постарайся отмечать настроение чаще, чтобы я мог дать тебе совет.");
            return;
        }

        String report;
        if (avgMood >= 4.0) {
            report = "Это была отличная неделя! Ты чувствовал себя на подъеме. Запиши, что именно сделало тебя счастливым, чтобы вернуться к этому в будущем.";
        } else if (avgMood >= 2.5) {
            report = "Твоя неделя была стабильной, но без ярких всплесков. Возможно, стоит добавить больше отдыха или встретиться с друзьями?";
        } else {
            report = "Кажется, неделя была тяжелой. Твое настроение было сниженным. Помни, что это временно. Попробуй описать свои тревоги ниже — это помогает.";
        }
        tvSummary.setText(report);
    }

    private void saveUserReflection() {
        // Получаем текст через inputReflection
        String text = inputReflection.getEditText().getText().toString().trim();

        if (text.isEmpty()) {
            inputReflection.setError("Пожалуйста, поделись своими чувствами");
            return;
        }

        inputReflection.setError(null); // Сброс ошибки

        Map<String, Object> reflectionData = new HashMap<>();
        reflectionData.put("text", text);
        reflectionData.put("timestamp", System.currentTimeMillis());

        userRef.child("weekly_reflections")
                .child(TimeManager.getCurrentDateKey())
                .setValue(reflectionData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Твоя история сохранена", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private List<String> getLastSevenDays() {
        List<String> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            dates.add(sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        return dates;
    }
}