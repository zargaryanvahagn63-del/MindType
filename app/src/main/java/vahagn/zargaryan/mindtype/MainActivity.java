package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.os.Bundle;
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
    QuestionAdapter adapter;
    TextView tvProgress;
    ProgressBar progress;
    Button btnFinish;

    List<Question> questions;
    int total = 20;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recycler);
        tvProgress = findViewById(R.id.tvProgress);
        progress = findViewById(R.id.progress);
        btnFinish = findViewById(R.id.btnFinish);

        questions = getQuestions();

        adapter = new QuestionAdapter(questions, (answered) -> {
            tvProgress.setText(answered + "/" + total);
            progress.setProgress((int) ((answered / (float) total) * 100));
            btnFinish.setEnabled(answered == total);
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnFinish.setEnabled(false);

        btnFinish.setOnClickListener(v -> {
            TestResult r = calculate(questions, adapter.answers);

            Intent i = new Intent(this, ResultActivity.class);
            i.putExtra("E", normalize(r.scores[0], 4));
            startActivity(i);
        });
    }

    private List<Question> getQuestions() {
        return Arrays.asList(
                new Question("Мне нравится быть в центре внимания", 0, false),
                new Question("Я быстро устаю от общения", 0, true),
                new Question("Я легко знакомлюсь", 0, false),
                new Question("Я предпочитаю одиночество", 0, true),

                new Question("Я стараюсь помогать людям", 1, false),
                new Question("Мне сложно сопереживать", 1, true),
                new Question("Я доверяю людям", 1, false),
                new Question("Я часто спорю", 1, true),

                new Question("Я довожу дела до конца", 2, false),
                new Question("Я часто откладываю", 2, true),
                new Question("Я организованный", 2, false),
                new Question("Я забываю о задачах", 2, true),

                new Question("Я часто переживаю", 3, false),
                new Question("Я спокоен в стрессе", 3, true),
                new Question("Я легко раздражаюсь", 3, false),
                new Question("Я редко тревожусь", 3, true),

                new Question("Мне нравятся новые идеи", 4, false),
                new Question("Я не люблю перемены", 4, true),
                new Question("Я люблю творчество", 4, false),
                new Question("Я избегаю нового опыта", 4, true)
        );
    }

    private TestResult calculate(List<Question> q, int[] answers) {
        TestResult r = new TestResult();
        for (int i = 0; i < q.size(); i++) {
            int val = answers[i];
            if (q.get(i).reverse) val = 6 - val;
            r.scores[q.get(i).trait] += val;
        }
        return r;
    }

    private int normalize(int raw, int count) {
        return (int) ((raw / (float) (count * 5)) * 100);
    }

    static class TestResult {
        int[] scores = new int[5];
    }
}
