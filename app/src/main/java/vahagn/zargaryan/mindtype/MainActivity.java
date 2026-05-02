package vahagn.zargaryan.mindtype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
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
        if (typeStr == null) { finish(); return; }
        type = TestType.valueOf(typeStr);

        // Инициализируем нужный анализатор
        initAnalyzer();

        // Получаем вопросы у анализатора
        questions = analyzer.getQuestions();
        total = questions.size();

        adapter = new QuestionAdapter(questions, this::updateProgress);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        Context context = recycler.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recycler.setAdapter(adapter);
        recycler.setLayoutAnimation(controller);
        recycler.scheduleLayoutAnimation();

        updateProgress(0);
        btnFinish.setOnClickListener(v -> finishTest());
    }

    private void initAnalyzer() {
        switch (type) {
            case MBTI:  analyzer = new MBTIAnalyzer(); break;
            case EQ:    analyzer = new EQAnalyzer(); break;
            case DARK3: analyzer = new DarkTriadAnalyzer(); break;
            case BIG5:  analyzer = new PersonalityAnalyzer(); break;
        }
    }

    private void updateProgress(int answered) {
        tvProgress.setText(answered + "/" + total);
        if (total > 0) {
            // Формула: 100 * sqrt(x / total)
            int p = (int) (100 * Math.sqrt((double) answered / total));
            progress.setProgress(p);
        }
        btnFinish.setVisibility(answered == total ? View.VISIBLE : View.GONE);
    }

    private void finishTest() {
        // 1. Прячем кнопку сразу, чтобы не нажали дважды
        btnFinish.setEnabled(false);

        // 2. Анимируем уход элементов (Recycler и Progress)
        recycler.animate().alpha(0f).translationY(-50f).setDuration(400).start();
        tvProgress.animate().alpha(0f).setDuration(400).start();
        progress.animate().alpha(0f).setDuration(400).start();

        // 3. Небольшая задержка "для солидности" (будто идет расчет)
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("type", type.name());

            int[] rawScores = calculateRaw();
            analyzer.packIntent(intent, rawScores);

            startActivity(intent);

            // Кастомный переход "растворение и масштаб"
            // (вместо стандартного системного сдвига)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }, 600);
    }

    private int[] calculateRaw() {
        int[] scores = new int[5]; // Максимум 5 шкал (для Big Five)
        for (int i = 0; i < questions.size(); i++) {
            int val = adapter.answers[i];
            if (val == -1) val = 2; // Нейтральный ответ, если пропустили
            if (questions.get(i).isReverse()) val = 4 - val;
            scores[questions.get(i).getTrait()] += val;
        }
        return scores;
    }
}