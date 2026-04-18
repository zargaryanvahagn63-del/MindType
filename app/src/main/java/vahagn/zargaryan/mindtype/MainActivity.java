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
    int total;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recycler);
        tvProgress = findViewById(R.id.tvProgress);
        progress = findViewById(R.id.progress);
        btnFinish = findViewById(R.id.btnFinish);

        questions = getQuestions();
        total = questions.size();

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

            i.putExtra("E", normalize(r.scores[0], 8));
            i.putExtra("A", normalize(r.scores[1], 8));
            i.putExtra("C", normalize(r.scores[2], 8));
            i.putExtra("N", normalize(r.scores[3], 8));
            i.putExtra("O", normalize(r.scores[4], 8));

            startActivity(i);
        });
    }

    private List<Question> getQuestions() {
        return Arrays.asList(
                new Question("Мне нравится общаться с людьми", 0, false),
                new Question("Я люблю шумные места", 0, false),
                new Question("Я легко начинаю разговор", 0, false),
                new Question("Мне нравится слушать", 0, false),
                new Question("Мне нравится быть в центре внимания", 0, false),
                new Question("Я устаю от общения", 0, true),
                new Question("Я предпочитаю быть один", 0, true),
                new Question("Я избегаю больших компаний", 0, true),
                new Question("Мне трудно заводить разговор", 0, true),

                new Question("Я стараюсь помогать другим", 1, false),
                new Question("Я добр к людям", 1, false),
                new Question("Я умею слушать", 1, false),
                new Question("Я доверяю людям", 1, false),
                new Question("Я часто спорю", 1, true),
                new Question("Мне трудно понять других", 1, true),
                new Question("Я редко помогаю", 1, true),
                new Question("Я не доверяю людям", 1, true),

                new Question("Я довожу дела до конца", 2,false),
                new Question("Я люблю порядок", 2, false),
                new Question("Я планирую дела", 2, false),
                new Question("Я ответственный", 2, false),
                new Question("Я часто откладываю", 2, true),
                new Question("Я забываю о делах", 2, true),
                new Question("У меня беспорядок", 2, true),
                new Question("Я делаю всё в последний момент", 2, true),

                new Question("Я часто переживаю", 3, false),
                new Question("Я легко злюсь", 3, false),
                new Question("Я быстро расстраиваюсь", 3, false),
                new Question("Я часто думаю о плохом", 3, false),
                new Question("Я спокоен в стрессе", 3, true),
                new Question("Меня трудно вывести из себя", 3, true),
                new Question("Я редко тревожусь", 3, true),
                new Question("Я быстро успокаиваюсь", 3, true),

                new Question("Мне нравятся новые идеи", 4, false),
                new Question("Я люблю пробовать новое", 4, false),
                new Question("Мне интересно учиться", 4, false),
                new Question("Я люблю творчество", 4, false),
                new Question("Я не люблю перемены", 4, true),
                new Question("Я избегаю нового", 4, true),
                new Question("Мне не интересно учиться", 4, true),
                new Question("Я не люблю творчество", 4, true)
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
