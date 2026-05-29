package vahagn.zargaryan.mindtype.analyzers;

import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vahagn.zargaryan.mindtype.tests.Question;

/**
 * Основной анализатор для теста "Большая Пятерка" (Big Five).
 * Оценивает пять ключевых черт личности: Экстраверсия, Доброжелательность,
 * Добросовестность, Нейротизм и Открытость опыту.
 */
public class PersonalityAnalyzer extends BaseAnalyzer {

    /**
     * Формирует подробный текстовый отчет по всем пяти шкалам.
     */
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
    public String getTitle(Intent intent) {
        return "Большая Пятерка";
    }

    @Override
    public String getMainResult(Intent intent) {
        Map<String, Integer> data = getChartData(intent);

        // Защита от отсутствия данных
        if (data == null || data.isEmpty()) {
            return "Личность: -";
        }

        // Ищем ключевую сильную черту характера из полной Большой Пятерки
        String topTrait = "";
        int highestVal = -1;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (entry.getValue() > highestVal) {
                highestVal = entry.getValue();
                topTrait = entry.getKey();
            }
        }

        return topTrait + ": " + highestVal + "%";
    }

    /**
     * Полный список вопросов для всех пяти шкал Big Five.
     */
    @Override
    public List<Question> getQuestions() {
        return Arrays.asList(
                // E (Экстраверсия)
                new Question("Мне нравится общаться с людьми", 0, false),
                new Question("Я легко начинаю разговор", 0, false),
                new Question("Я чувствую себя комфортно в компании", 0, false),
                new Question("Я люблю быть в центре внимания", 0, false),
                new Question("Я устаю от общения", 0, true),
                new Question("Я предпочитаю одиночество", 0, true),
                new Question("Я избегаю больших компаний", 0, true),
                new Question("Мне трудно заводить новые знакомства", 0, true),

                // A (Доброжелательность)
                new Question("Я стараюсь помогать другим", 1, false),
                new Question("Я доверяю людям", 1, false),
                new Question("Я часто проявляю эмпатию", 1, false),
                new Question("Я стараюсь избегать конфликтов", 1, false),
                new Question("Я часто спорю", 1, true),
                new Question("Мне сложно понимать других", 1, true),
                new Question("Я бываю равнодушным к проблемам других", 1, true),
                new Question("Я не склонен помогать без выгоды", 1, true),

                // C (Добросовестность)
                new Question("Я довожу дела до конца", 2, false),
                new Question("Я люблю порядок и структуру", 2, false),
                new Question("Я планирую свои действия заранее", 2, false),
                new Question("Я ответственно отношусь к задачам", 2, false),
                new Question("Я часто откладываю дела", 2, true),
                new Question("Я действую импульсивно", 2, true),
                new Question("Я легко отвлекаюсь", 2, true),
                new Question("Я забываю о важных делах", 2, true),

                // N (Нейротизм)
                new Question("Я часто переживаю", 3, false),
                new Question("Я легко тревожусь", 3, false),
                new Question("Я быстро расстраиваюсь", 3, false),
                new Question("Я часто думаю о плохом", 3, false),
                new Question("Я остаюсь спокойным в стрессовых ситуациях", 3, true),
                new Question("Меня трудно вывести из себя", 3, true),
                new Question("Я редко испытываю тревогу", 3, true),
                new Question("Я быстро восстанавливаюсь после стресса", 3, true),

                // O (Открытость опыту)
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

    /**
     * Сбор данных для построения графика по всем пяти осям.
     */
    @Override
    public Map<String, Integer> getChartData(Intent data) {
        Map<String, Integer> chartData = new LinkedHashMap<>();
        chartData.put("Экстраверсия", data.getIntExtra("E", 0));
        chartData.put("Дружелюбие", data.getIntExtra("A", 0));
        chartData.put("Сознательность", data.getIntExtra("C", 0));
        chartData.put("Нейротизм", data.getIntExtra("N", 0));
        chartData.put("Открытость", data.getIntExtra("O", 0));
        return chartData;
    }

    /**
     * Нормализация баллов в проценты для хранения и передачи.
     */
    @Override
    public void packIntent(Intent intent, int[] scores) {
        float maxPerTrait = 32f; // 8 вопросов * 4 балла
        intent.putExtra("E", (int) ((scores[0] / maxPerTrait) * 100));
        intent.putExtra("A", (int) ((scores[1] / maxPerTrait) * 100));
        intent.putExtra("C", (int) ((scores[2] / maxPerTrait) * 100));
        intent.putExtra("N", (int) ((scores[3] / maxPerTrait) * 100));
        intent.putExtra("O", (int) ((scores[4] / maxPerTrait) * 100));
    }

    /**
     * Вспомогательный метод для сохранения частичных результатов Big Five в Firebase.
     * Использует updateChildren для объединения данных разных частей теста.
     */
    protected static void savePartResultsToFirebase(Map<String, Integer> results) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || results == null || results.isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("personalityResults");

        Map<String, Object> updates = new HashMap<>();
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            updates.put(entry.getKey(), entry.getValue());
        }

        ref.updateChildren(updates);
    }

    // Методы генерации описаний для каждой шкалы

    protected String getExtraDesc(int v) {
        if (v > 60) return "Вы открыты миру и черпаете энергию в общении.";
        if (v < 40) return "Вы интровертны и цените время наедине с собой.";
        return "Вы находите баланс между людьми и одиночеством.";
    }

    protected String getAgreeDesc(int v) {
        if (v > 60) return "Вы доверчивы и всегда готовы прийти на помощь.";
        if (v < 40) return "Вы прямолинейны и критичны в суждениях.";
        return "Вы умеете находить компромиссы.";
    }

    protected String getConscienceDesc(int v) {
        if (v > 60) return "Вы дисциплинированы и всегда доводите дела до конца.";
        if (v < 40) return "Вы спонтанны и не любите жестких рамок.";
        return "Вы организованы, но гибки в планировании.";
    }

    protected String getNeuroDesc(int v) {
        if (v > 60) return "Вы эмоционально чувствительны к стрессу.";
        if (v < 40) return "Вы эмоционально стабильны и хладнокровны.";
        return "Вы умеренно реагируете на жизненные трудности.";
    }

    protected String getOpenDesc(int v) {
        if (v > 60) return "Вы обожаете новые идеи и творчество.";
        if (v < 40) return "Вы консервативны и цените проверенные методы.";
        return "Вы открыты новому, но не забываете о традициях.";
    }

    /**
     * Анализатор для первой части Большой Пятерки.
     * Оценивает Экстраверсию, Доброжелательность и Сознательность.
     */
    public static class BigFivePart1Analyzer extends PersonalityAnalyzer {

        @Override
        public List<Question> getQuestions() {
            // Первые три шкалы (24 вопроса)
            return super.getQuestions().subList(0, 24);
        }

        @Override
        public String getTitle(Intent intent) {
            return "Большая Пятерка: Часть 1";
        }

        @Override
        public String getMainResult(Intent intent) {
            // Для промежуточного этапа выводим простой статус завершения
            return "Часть 1: Пройдена";
        }

        @Override
        public void packIntent(Intent intent, int[] scores) {
            float maxScore = 32f;
            intent.putExtra("E", (int) ((scores[0] / maxScore) * 100));
            intent.putExtra("A", (int) ((scores[1] / maxScore) * 100));
            intent.putExtra("C", (int) ((scores[2] / maxScore) * 100));
            intent.putExtra("test_type", "BIG5_PART1");
        }

        @Override
        public Map<String, Integer> getChartData(Intent data) {
            Map<String, Integer> chartData = new LinkedHashMap<>();
            chartData.put("Экстраверсия", data.getIntExtra("E", 0));
            chartData.put("Дружелюбие", data.getIntExtra("A", 0));
            chartData.put("Сознательность", data.getIntExtra("C", 0));
            return chartData;
        }

        @Override
        public String getAnalysis(Intent data) {
            int e = data.getIntExtra("E", 0);
            int a = data.getIntExtra("A", 0);
            int c = data.getIntExtra("C", 0);

            return "Результаты Большой Пятерки (Часть 1):\n\n" +
                    "Экстраверсия: " + e + "% — " + getExtraDesc(e) + "\n\n" +
                    "Доброжелательность: " + a + "% — " + getAgreeDesc(a) + "\n\n" +
                    "Добросовестность: " + c + "% — " + getConscienceDesc(c);
        }

        @Override
        public void saveResultsToFirebase(Map<String, Integer> results) {
            savePartResultsToFirebase(results);
        }
    }

    /**
     * Анализатор для второй части Большой Пятерки.
     * Оценивает Нейротизм и Открытость опыту, а при наличии первой части — строит полный профиль.
     */
    public static class BigFivePart2Analyzer extends PersonalityAnalyzer {

        @Override
        public List<Question> getQuestions() {
            // Оставшиеся две шкалы (16 вопросов)
            return super.getQuestions().subList(24, 40);
        }

        @Override
        public void packIntent(Intent intent, int[] scores) {
            float maxScore = 32f;
            int n = 0, o = 0;
            if (scores != null) {
                if (scores.length > 4) {
                    n = (int) ((scores[3] / maxScore) * 100);
                    o = (int) ((scores[4] / maxScore) * 100);
                } else if (scores.length > 1) {
                    n = (int) ((scores[0] / maxScore) * 100);
                    o = (int) ((scores[1] / maxScore) * 100);
                }
            }
            intent.putExtra("N", n);
            intent.putExtra("O", o);
            intent.putExtra("test_type", "BIG5_PART2");
        }

        @Override
        public String getTitle(Intent intent) {
            // Если данные объединены, пишем красивый заголовок полного теста
            if (intent.hasExtra("E")) {
                return "Большая Пятерка (Полный профиль)";
            }
            return "Большая Пятерка: Часть 2";
        }

        @Override
        public String getMainResult(Intent intent) {
            Map<String, Integer> data = getChartData(intent);

            if (data == null || data.isEmpty()) {
                return "BigFive: -";
            }

            // Ищем доминирующую черту характера среди всех доступных в графике
            String dominantTrait = "";
            int maxPercent = -1;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                if (entry.getValue() > maxPercent) {
                    maxPercent = entry.getValue();
                    dominantTrait = entry.getKey();
                }
            }

            return dominantTrait + ": " + maxPercent + "%";
        }

        @Override
        public Map<String, Integer> getChartData(Intent data) {
            // Если в интенте есть данные первого этапа, строим полноценный 5-осевой график!
            if (data.hasExtra("E")) {
                return super.getChartData(data); // Вызывает метод родителя с 5 шкалами
            }

            // Фолбек на случай, если данные не слились (только 2 оси)
            Map<String, Integer> chartData = new LinkedHashMap<>();
            chartData.put("Нейротизм", data.getIntExtra("N", 0));
            chartData.put("Открытость", data.getIntExtra("O", 0));
            return chartData;
        }

        @Override
        public String getAnalysis(Intent data) {
            // Если данные объединены, генерируем полный отчет по всем 5 шкалам
            if (data.hasExtra("E")) {
                return super.getAnalysis(data); // Вызывает метод родителя с полным текстом
            }

            int n = data.getIntExtra("N", 0);
            int o = data.getIntExtra("O", 0);
            return "Результаты Большой Пятерки (Часть 2):\n\n" +
                    "Нейротизм: " + n + "% — " + getNeuroDesc(n) + "\n\n" +
                    "Открытость: " + o + "% — " + getOpenDesc(o);
        }

        @Override
        public void saveResultsToFirebase(Map<String, Integer> results) {
            savePartResultsToFirebase(results);
        }
    }
}