package vahagn.zargaryan.mindtype.analyzers;

import android.content.Intent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vahagn.zargaryan.mindtype.tests.Question;
import vahagn.zargaryan.mindtype.tests.QuestionGenerator;

/**
 * Анализатор для теста "Темная триада".
 * Оценивает три черты: Нарциссизм, Макиавеллизм и Психопатию.
 */
public class DarkTriadAnalyzer extends BaseAnalyzer {

    /**
     * Формирует текстовый анализ на основе результатов теста.
     * @param data Intent с баллами по шкалам.
     * @return Форматированная строка с описанием черт.
     */
    @Override
    public String getAnalysis(Intent data) {
        // Извлечение процентов выраженности черт
        int narc = data.getIntExtra("NARCISSISM", 0);
        int mach = data.getIntExtra("MACHIAVELLIANISM", 0);
        int psy = data.getIntExtra("PSYCHOPATHY", 0);

        StringBuilder sb = new StringBuilder();

        // Анализ каждой черты с использованием уровней (getLevel)
        sb.append("Нарциссизм: ").append(narc).append("% (").append(getLevel(narc)).append(")\n");
        sb.append(getNarcText(narc)).append("\n\n");

        sb.append("Макиавеллизм: ").append(mach).append("% (").append(getLevel(mach)).append(")\n");
        sb.append(getMachText(mach)).append("\n\n");

        sb.append("Психопатия: ").append(psy).append("% (").append(getLevel(psy)).append(")\n");
        sb.append(getPsyText(psy)).append("\n\n");

        // Общий интегральный вывод
        int avg = (narc + mach + psy) / 3;
        sb.append("Общий профиль:\n");
        if (avg > 70) sb.append("У вас ярко выражены черты «Темной триады». Вы склонны к риску и холодному расчету.");
        else if (avg > 40) sb.append("Ваши показатели сбалансированы. Вы умеете постоять за себя, не переходя грани.");
        else sb.append("У вас низкий уровень «темных» черт. Вы искренни и доверчивы.");

        return sb.toString();
    }

    @Override
    public String getTitle(Intent data) { return "Темная Триада"; }

    @Override
    public String getMainResult(Intent intent) {
        Map<String, Integer> data = getChartData(intent);

        // Защита от отсутствия данных
        if (data == null || data.isEmpty()) {
            return "Темность: 0%";
        }

        // Находим средний показатель «темности» характера (по трем шкалам)
        int sum = 0;
        for (int score : data.values()) {
            sum += score;
        }
        int average = sum / data.size();

        return "Темность: " + average + "%";
    }

    /**
     * Генерирует вопросы для теста Dark Triad.
     */
    @Override
    public List<Question> getQuestions() {
        return QuestionGenerator.generateDarkTriad(5, QuestionGenerator.Level.NORMAL);
    }

    /**
     * Подготавливает данные для графика (Spider Chart).
     */
    @Override
    public Map<String, Integer> getChartData(Intent data) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Нарциссизм", data.getIntExtra("NARCISSISM", 0));
        map.put("Макиавеллизм", data.getIntExtra("MACHIAVELLIANISM", 0));
        map.put("Психопатия", data.getIntExtra("PSYCHOPATHY", 0));
        return map;
    }

    /**
     * Упаковывает баллы в Intent, переводя их в проценты.
     * @param intent Целевой Intent.
     * @param scores Массив баллов (предполагается 5 вопросов на шкалу, макс 20 баллов).
     */
    @Override
    public void packIntent(Intent intent, int[] scores) {
        intent.putExtra("NARCISSISM", (int) ((scores[0] / 20f) * 100));
        intent.putExtra("MACHIAVELLIANISM", (int) ((scores[1] / 20f) * 100));
        intent.putExtra("PSYCHOPATHY", (int) ((scores[2] / 20f) * 100));
    }

    // Вспомогательные методы для получения описаний в зависимости от баллов

    private String getNarcText(int v) {
        if (v >= 70) return "Вы стремитесь к признанию и уверены в своей исключительности.";
        if (v < 40) return "Вы скромны и не стремитесь быть в центре внимания.";
        return "У вас здоровая самооценка.";
    }

    private String getMachText(int v) {
        if (v >= 70) return "Вы стратег, умеющий влиять на людей ради достижения цели.";
        if (v < 40) return "Вы сторонник честной игры и открытости.";
        return "Вы прагматичны, но соблюдаете моральные нормы.";
    }

    private String getPsyText(int v) {
        if (v >= 70) return "Вы хладнокровны, импульсивны и любите риск.";
        if (v < 40) return "Вы очень осторожны, эмпатичны и контролируете свои порывы.";
        return "Вы способны сохранять спокойствие, не теряя связи с чувствами.";
    }
}
