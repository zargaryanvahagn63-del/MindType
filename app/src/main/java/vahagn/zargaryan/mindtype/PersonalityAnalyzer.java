package vahagn.zargaryan.mindtype;

import android.content.Intent;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersonalityAnalyzer extends BaseAnalyzer {
    @Override
    public String getAnalysis(Intent data) {
        int e = data.getIntExtra("E", 0);
        int a = data.getIntExtra("A", 0);
        int c = data.getIntExtra("C", 0);
        int n = data.getIntExtra("N", 0);
        int o = data.getIntExtra("O", 0);

        return "Результаты Большой Пятерки:\n\n" +
                "Экстраверсия: " + e + "% — " + getExtraDesc(e) + "\n\n" +
                "Доброжелательность: " + a + "% — " + getAgreeDesc(a) + "\n\n" +
                "Добросовестность: " + c + "% — " + getConscienceDesc(c) + "\n\n" +
                "Нейротизм: " + n + "% — " + getNeuroDesc(n) + "\n\n" +
                "Открытость: " + o + "% — " + getOpenDesc(o);
    }

    @Override
    public List<Question> getQuestions() {
        // Возвращаем тот самый жестко заданный список, который раньше был в MainActivity
        return Arrays.asList(
                // E
                new Question("Мне нравится общаться с людьми", 0, false),
                new Question("Я легко начинаю разговор", 0, false),
                new Question("Я чувствую себя комфортно в компании", 0, false),
                new Question("Я люблю быть в центре внимания", 0, false),
                new Question("Я устаю от общения", 0, true),
                new Question("Я предпочитаю одиночество", 0, true),
                new Question("Я избегаю больших компаний", 0, true),
                new Question("Мне трудно заводить новые знакомства", 0, true),

                // A
                new Question("Я стараюсь помогать другим", 1, false),
                new Question("Я доверяю людям", 1, false),
                new Question("Я часто проявляю эмпатию", 1, false),
                new Question("Я стараюсь избегать конфликтов", 1, false),
                new Question("Я часто спорю", 1, true),
                new Question("Мне сложно понимать других", 1, true),
                new Question("Я бываю равнодушным к проблемам других", 1, true),
                new Question("Я не склонен помогать без выгоды", 1, true),

                // C
                new Question("Я довожу дела до конца", 2, false),
                new Question("Я люблю порядок и структуру", 2, false),
                new Question("Я планирую свои действия заранее", 2, false),
                new Question("Я ответственно отношусь к задачам", 2, false),
                new Question("Я часто откладываю дела", 2, true),
                new Question("Я действую импульсивно", 2, true),
                new Question("Я легко отвлекаюсь", 2, true),
                new Question("Я забываю о важных делах", 2, true),

                // N
                new Question("Я часто переживаю", 3, false),
                new Question("Я легко тревожусь", 3, false),
                new Question("Я быстро расстраиваюсь", 3, false),
                new Question("Я часто думаю о плохом", 3, false),
                new Question("Я остаюсь спокойным в стрессовых ситуациях", 3, true),
                new Question("Меня трудно вывести из себя", 3, true),
                new Question("Я редко испытываю тревогу", 3, true),
                new Question("Я быстро восстанавливаюсь после стресса", 3, true),

                // O
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

    @Override
    public Map<String, Integer> getChartData(Intent data) {
        Map<String, Integer> chartData = new LinkedHashMap<>();

        // Достаем данные по тем же ключам, которые использовали в packIntent
        chartData.put("Экстраверсия", data.getIntExtra("E", 0));
        chartData.put("Дружелюбие", data.getIntExtra("A", 0));
        chartData.put("Сознательность", data.getIntExtra("C", 0));
        chartData.put("Нейротизм", data.getIntExtra("N", 0));
        chartData.put("Открытость", data.getIntExtra("O", 0));

        return chartData;
    }

    @Override
    public void packIntent(Intent intent, int[] scores) {
        // Здесь делим на 32, так как вопросов 8
        intent.putExtra("E", (int) ((scores[0] / 32f) * 100));
        intent.putExtra("A", (int) ((scores[1] / 32f) * 100));
        intent.putExtra("C", (int) ((scores[2] / 32f) * 100));
        intent.putExtra("N", (int) ((scores[3] / 32f) * 100));
        intent.putExtra("O", (int) ((scores[4] / 32f) * 100));
    }

    private String getExtraDesc(int v) {
        if (v > 60) return "Вы открыты миру и черпаете энергию в общении.";
        if (v < 40) return "Вы интровертны и цените время наедине с собой.";
        return "Вы находите баланс между людьми и одиночеством.";
    }

    private String getAgreeDesc(int v) {
        if (v > 60) return "Вы доверчивы и всегда готовы прийти на помощь.";
        if (v < 40) return "Вы прямолинейны и критичны в суждениях.";
        return "Вы умеете находить компромиссы.";
    }

    private String getConscienceDesc(int v) {
        if (v > 60) return "Вы дисциплинированы и всегда доводите дела до конца.";
        if (v < 40) return "Вы спонтанны и не любите жестких рамок.";
        return "Вы организованы, но гибки в планировании.";
    }

    private String getNeuroDesc(int v) {
        if (v > 60) return "Вы эмоционально чувствительны к стрессу.";
        if (v < 40) return "Вы эмоционально стабильны и хладнокровны.";
        return "Вы умеренно реагируете на жизненные трудности.";
    }

    private String getOpenDesc(int v) {
        if (v > 60) return "Вы обожаете новые идеи и творчество.";
        if (v < 40) return "Вы консервативны и цените проверенные методы.";
        return "Вы открыты новому, но не забываете о традициях.";
    }
}