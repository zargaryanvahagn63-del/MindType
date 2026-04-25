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
    TextView tvProgress;
    ProgressBar progress;
    Button btnFinish;

    QuestionAdapter adapter;
    FunAdapter funAdapter;

    List<Question> questions;
    List<CharacterQuestion> funQuestions;

    int total;
    TestType type;

    List<Character> characters;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recycler);
        tvProgress = findViewById(R.id.tvProgress);
        progress = findViewById(R.id.progress);
        btnFinish = findViewById(R.id.btnFinish);

        btnFinish.setEnabled(false);

        type = TestType.valueOf(getIntent().getStringExtra("type"));

        switch (type) {
            case FUN:
                setupFun();
                break;
            case MBTI:
                setupMBTI();
                break;
            case BIG5:
                setupBigFive();
                break;
            case DARK3:
                setupDark3();
                break;
        }

        recycler.post(() -> updateProgress(total));

        btnFinish.setOnClickListener(v -> finishTest());
    }

    private void setupBigFive() {
        questions = getBigFive();
        total = questions.size();

        adapter = new QuestionAdapter(questions, this::updateProgress);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        updateProgress(0);
    }

    private void setupMBTI() {
        questions = QuestionGenerator.generateMBTI(5, QuestionGenerator.Level.NORMAL);
        total = questions.size();

        adapter = new QuestionAdapter(questions, this::updateProgress);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        updateProgress(0);
    }

    private void setupFun() {
        characters = Arrays.asList(
                new Character("Крош", R.mipmap.crosh),
                new Character("Ёжик", R.mipmap.egik),
                new Character("Лосяш", R.mipmap.losash),
                new Character("Нюша", R.mipmap.nyusha)
        );

        funQuestions = Arrays.asList(

                new CharacterQuestion("Как ты проводишь день?", Arrays.asList(
                        new Choice("Активно", 0),
                        new Choice("Спокойно", 1),
                        new Choice("Учусь", 2),
                        new Choice("Слежу за собой", 3)
                )),

                new CharacterQuestion("Твое настроение?", Arrays.asList(
                        new Choice("Весёлое", 0),
                        new Choice("Тихое", 1),
                        new Choice("Задумчивое", 2),
                        new Choice("Романтичное", 3)
                )),

                new CharacterQuestion("Ты в компании?", Arrays.asList(
                        new Choice("Шучу", 0),
                        new Choice("Слушаю", 1),
                        new Choice("Объясняю", 2),
                        new Choice("Флиртую", 3)
                )),

                new CharacterQuestion("Твое хобби?", Arrays.asList(
                        new Choice("Спорт", 0),
                        new Choice("Чтение", 1),
                        new Choice("Наука", 2),
                        new Choice("Красота", 3)
                )),

                new CharacterQuestion("Как принимаешь решения?", Arrays.asList(
                        new Choice("Быстро", 0),
                        new Choice("Осторожно", 1),
                        new Choice("Логично", 2),
                        new Choice("По чувствам", 3)
                )),

                new CharacterQuestion("Ты чаще?", Arrays.asList(
                        new Choice("Активный", 0),
                        new Choice("Спокойный", 1),
                        new Choice("Умный", 2),
                        new Choice("Милый", 3)
                )),

                new CharacterQuestion("Твоя реакция на стресс?", Arrays.asList(
                        new Choice("Действую", 0),
                        new Choice("Ухожу в себя", 1),
                        new Choice("Анализирую", 2),
                        new Choice("Переживаю", 3)
                )),

                new CharacterQuestion("Тебе ближе?", Arrays.asList(
                        new Choice("Движение", 0),
                        new Choice("Тишина", 1),
                        new Choice("Идеи", 2),
                        new Choice("Эмоции", 3)
                )),

                new CharacterQuestion("Как отдыхаешь?", Arrays.asList(
                        new Choice("Активно", 0),
                        new Choice("Один", 1),
                        new Choice("Учусь", 2),
                        new Choice("Мечтаю", 3)
                )),

                new CharacterQuestion("Твой стиль?", Arrays.asList(
                        new Choice("Простой", 0),
                        new Choice("Скромный", 1),
                        new Choice("Умный", 2),
                        new Choice("Яркий", 3)
                ))
        );

        total = funQuestions.size();

        funAdapter = new FunAdapter(funQuestions, this::updateProgress);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(funAdapter);

        updateProgress(0);
    }

    private void setupDark3() {
        questions = Arrays.asList(

                // Нарциссизм
                new Question("Я люблю быть в центре внимания", 0, false),
                new Question("Я считаю себя особенным", 0, false),
                new Question("Мне важно восхищение", 0, false),
                new Question("Я лучше большинства", 0, false),
                new Question("Мне не важно мнение других", 0, true),
                new Question("Я не стремлюсь выделяться", 0, true),
                new Question("Я обычный человек", 0, true),
                new Question("Я избегаю внимания", 0, true),

                // Макиавеллизм
                new Question("Людьми можно манипулировать", 1, false),
                new Question("Цель оправдывает средства", 1, false),
                new Question("Я умею использовать людей", 1, false),
                new Question("Я скрываю свои намерения", 1, false),
                new Question("Я всегда честен", 1, true),
                new Question("Я не люблю хитрость", 1, true),
                new Question("Я открыт с людьми", 1, true),
                new Question("Манипуляции — это плохо", 1, true),

                // Психопатия
                new Question("Я редко чувствую вину", 2, false),
                new Question("Я люблю риск", 2, false),
                new Question("Я действую импульсивно", 2, false),
                new Question("Я холоден к чувствам других", 2, false),
                new Question("Я переживаю за других", 2, true),
                new Question("Я избегаю риска", 2, true),
                new Question("Я контролирую себя", 2, true),
                new Question("Я сочувствую людям", 2, true)
        );

        total = questions.size();

        adapter = new QuestionAdapter(questions, this::updateProgress);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        updateProgress(0);
    }

    private void updateProgress(int answered) {
        tvProgress.setText(answered + "/" + total);
        progress.setProgress((int) ((answered / (float) total) * 100));
        btnFinish.setEnabled(answered == total && total > 0);
    }

    private void finishTest() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("type", type.name());

        switch (type) {
            case FUN:
                handleFun(intent);
                break;
            case MBTI:
                handleMBTI(intent);
                break;
            case BIG5:
                handleBigFive(intent);
                break;
            case DARK3:
                handleDark3(intent);
                break;
        }

        startActivity(intent);
    }

    private void handleMBTI(Intent intent) {
        int[] scores = calculateRaw(questions, adapter.answers);
        intent.putExtra("E", normalize(scores[0], 5));
        intent.putExtra("S", normalize(scores[1], 5));
        intent.putExtra("T", normalize(scores[2], 5));
        intent.putExtra("J", normalize(scores[3], 5));
    }

    private void handleBigFive(Intent intent) {
        TestResult r = calculate(questions, adapter.answers);
        intent.putExtra("E", normalize(r.scores[0], 8));
        intent.putExtra("A", normalize(r.scores[1], 8));
        intent.putExtra("C", normalize(r.scores[2], 8));
        intent.putExtra("N", normalize(r.scores[3], 8));
        intent.putExtra("O", normalize(r.scores[4], 8));
    }

    private void handleDark3(Intent intent) {
        TestResult r = calculate(questions, adapter.answers);
        intent.putExtra("NARC", normalize(r.scores[0], 8));
        intent.putExtra("MACH", normalize(r.scores[1], 8));
        intent.putExtra("PSY", normalize(r.scores[2], 8));
    }

    private void handleFun(Intent intent) {
        int[] answers = funAdapter.answers;

        for (int i = 0; i < answers.length; i++) {
            int choiceIndex = answers[i];
            if (choiceIndex == -1) continue;

            Choice c = funQuestions.get(i).choices.get(choiceIndex);
            characters.get(c.characterIndex).score++;
        }

        String[] names = new String[characters.size()];
        int[] percents = new int[characters.size()];
        int[] images = new int[characters.size()];

        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            names[i] = c.name;
            percents[i] = c.score * 100 / answers.length;
            images[i] = c.imageRes;
        }

        intent.putExtra("names", names);
        intent.putExtra("percents", percents);
        intent.putExtra("images", images);
    }

    private int[] calculateRaw(List<Question> q, int[] answers) {
        int[] scores = new int[4];
        for (int i = 0; i < q.size(); i++) {
            int val = answers[i];
            if (q.get(i).reverse) val = 4 - val;
            scores[q.get(i).trait] += val;
        }
        return scores;
    }

    private TestResult calculate(List<Question> q, int[] answers) {
        TestResult r = new TestResult();
        for (int i = 0; i < q.size(); i++) {
            int val = answers[i];
            if (q.get(i).reverse) val = 4 - val;
            r.scores[q.get(i).trait] += val;
        }
        return r;
    }

    private int normalize(int raw, int count) {
        return (int) ((raw / (float) (count * 4)) * 100);
    }

    private List<Question> getBigFive() {
        return Arrays.asList(
                // E (Extraversion)
                new Question("Мне нравится общаться с людьми", 0, false),
                new Question("Я легко начинаю разговор", 0, false),
                new Question("Я чувствую себя комфортно в компании", 0, false),
                new Question("Я люблю быть в центре внимания", 0, false),
                new Question("Я устаю от общения", 0, true),
                new Question("Я предпочитаю одиночество", 0, true),
                new Question("Я избегаю больших компаний", 0, true),
                new Question("Мне трудно заводить новые знакомства", 0, true),

                // A (Agreeableness)
                new Question("Я стараюсь помогать другим", 1, false),
                new Question("Я доверяю людям", 1, false),
                new Question("Я часто проявляю эмпатию", 1, false),
                new Question("Я стараюсь избегать конфликтов", 1, false),
                new Question("Я часто спорю", 1, true),
                new Question("Мне сложно понимать других", 1, true),
                new Question("Я бываю равнодушным к проблемам других", 1, true),
                new Question("Я не склонен помогать без выгоды", 1, true),

                // C (Conscientiousness)
                new Question("Я довожу дела до конца", 2, false),
                new Question("Я люблю порядок и структуру", 2, false),
                new Question("Я планирую свои действия заранее", 2, false),
                new Question("Я ответственно отношусь к задачам", 2, false),
                new Question("Я часто откладываю дела", 2, true),
                new Question("Я действую импульсивно", 2, true),
                new Question("Я легко отвлекаюсь", 2, true),
                new Question("Я забываю о важных делах", 2, true),

                // N (Neuroticism)
                new Question("Я часто переживаю", 3, false),
                new Question("Я легко тревожусь", 3, false),
                new Question("Я быстро расстраиваюсь", 3, false),
                new Question("Я часто думаю о плохом", 3, false),
                new Question("Я остаюсь спокойным в стрессовых ситуациях", 3, true),
                new Question("Меня трудно вывести из себя", 3, true),
                new Question("Я редко испытываю тревогу", 3, true),
                new Question("Я быстро восстанавливаюсь после стресса", 3, true),

                // O (Openness)
                new Question("Мне нравятся новые идеи", 4, false),
                new Question("Я люблю учиться новому", 4, false),
                new Question("Мне интересны абстрактные темы", 4, false),
                new Question("Я люблю творчество и фантазию", 4, false),
                new Question("Я не люблю перемены", 4, true),
                new Question("Мне сложно принимать новое", 4, true),
                new Question("Я предпочитаю привычные вещи", 4, true),
                new Question("Я избегаю сложных идей", 4, true)
        );


    }
    class TestResult {
        int[] scores = new int[5];
    }
}