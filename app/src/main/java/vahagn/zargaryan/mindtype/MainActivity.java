package vahagn.zargaryan.mindtype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    TextView tvProgress;
    ProgressBar progress;
    Button btnFinish;

    QuestionAdapter adapter;
    List<Question> questions;
    BaseAnalyzer analyzer;
    TestType type;
    int total;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recycler);
        tvProgress = findViewById(R.id.tvProgress);
        progress = findViewById(R.id.progress);
        btnFinish = findViewById(R.id.btnFinish);

        String typeStr = getIntent().getStringExtra("type");
        if (typeStr == null) {
            finish();
            return;
        }

        // Защита от кривого типа теста
        try {
            type = TestType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            finish();
            return;
        }

        initAnalyzer();

        // ЖЕСТКАЯ ПРОВЕРКА НА NULL (чтобы не было вылетов)
        if (analyzer != null) {
            questions = analyzer.getQuestions();
            if (questions == null || questions.isEmpty()) {
                finish();
                return;
            }
            total = questions.size();
        } else {
            Log.e("MainActivity", "Analyzer is null for type: " + type);
            finish();
            return;
        }

        adapter = new QuestionAdapter(questions, this::updateProgress);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        Context context = recycler.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recycler.setAdapter(adapter);
        recycler.setLayoutAnimation(controller);
        recycler.scheduleLayoutAnimation();

        updateProgress(0);
        btnFinish.setOnClickListener(v -> finishTest());

        MoodAlarmScheduler.scheduleAllAlarms(this);
    }

    private void initAnalyzer() {
        switch (type) {
            case MBTI:  analyzer = new MBTIAnalyzer(); break;
            // Убедись, что классы ниже существуют в твоем проекте.
            // Если нет — закомментируй их.
            case EQ:    analyzer = new EQAnalyzer(); break;
            case DARK3: analyzer = new DarkTriadAnalyzer(); break;
            case BIG5:  analyzer = new PersonalityAnalyzer(); break;
            case VARK:  analyzer = new VarkAnalyzer(); break;
        }
    }

    private void updateProgress(int answered) {
        tvProgress.setText(answered + "/" + total);
        if (total > 0) {
            int p = (int) (100 * Math.sqrt((double) answered / total));
            progress.setProgress(p);
        }
        btnFinish.setVisibility(answered == total ? View.VISIBLE : View.GONE);
    }

    private void finishTest() {
        btnFinish.setEnabled(false);

        recycler.animate().alpha(0f).translationY(-50f).setDuration(400).start();
        tvProgress.animate().alpha(0f).setDuration(400).start();
        progress.animate().alpha(0f).setDuration(400).start();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("type", type.name());

            int[] rawScores = calculateRaw();
            analyzer.packIntent(intent, rawScores);

            String uid = FirebaseAuth.getInstance().getUid();
            if (uid != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid);

                if (type == TestType.MBTI) {
                    String typeCode = intent.getStringExtra("MBTI_TYPE");
                    if (typeCode != null) {
                        ref.child("mbtiType").setValue(typeCode);
                        ref.child("mbtiDone").setValue(true);
                    }
                }

                updateTestsCount(ref);
            }

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Важно закрыть MainActivity после перехода

        }, 600);

        XpManager.addXp(XpManager.XP_REWARD_TEST, (newXp, leveledUp) -> {
            if (leveledUp) {
                Toast.makeText(getApplicationContext(), "Поздравляем! Ваш ранг повышен!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTestsCount(DatabaseReference userRef) {
        userRef.child("testsCount").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer count = currentData.getValue(Integer.class);
                if (count == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(count + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e("FB_ERROR", "Ошибка обновления счетчика: " + error.getMessage());
                }
            }
        });
    }

    private int[] calculateRaw() {
        int[] scores = new int[5];
        for (int i = 0; i < questions.size(); i++) {
            int val = adapter.answers[i];
            if (val == -1) val = 2;
            if (questions.get(i).isReverse()) val = 4 - val;
            scores[questions.get(i).getTrait()] += val;
        }
        return scores;
    }
}