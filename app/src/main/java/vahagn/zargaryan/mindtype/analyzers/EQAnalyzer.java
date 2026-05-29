package vahagn.zargaryan.mindtype.analyzers;

import android.content.Intent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vahagn.zargaryan.mindtype.tests.Question;
import vahagn.zargaryan.mindtype.tests.QuestionGenerator;

/**
 * Анализатор для теста на эмоциональный интеллект (EQ).
 * Оценивает три ключевых параметра: Самопознание, Социальные навыки и Эмпатия.
 */
public class EQAnalyzer extends BaseAnalyzer {

    /**
     * Формирует подробный текстовый отчет на основе результатов теста.
     * @param data Intent с баллами по шкалам.
     * @return Форматированная строка с анализом.
     */
    @Override
    public String getAnalysis(Intent data) {
        // Извлечение баллов из Intent (значения от 0 до 100)
        int self = data.getIntExtra("SELF_AWARE", 0);
        int social = data.getIntExtra("SOCIAL_SKILLS", 0);
        int empathy = data.getIntExtra("EMPATHY", 0);

        StringBuilder sb = new StringBuilder();

        // Секция Самопознания
        sb.append("Самопознание: ").append(self).append("% (").append(getLevel(self)).append(")\n");
        sb.append(getSelfText(self)).append("\n\n");

        // Секция Социальных навыков
        sb.append("Социальные навыки: ").append(social).append("% (").append(getLevel(social)).append(")\n");
        sb.append(getSocialText(social)).append("\n\n");

        // Секция Эмпатии
        sb.append("Эмпатия: ").append(empathy).append("% (").append(getLevel(empathy)).append(")\n");
        sb.append(getEmpathyText(empathy)).append("\n\n");

        // Расчет общего среднего уровня EQ
        int total = (self + social + empathy) / 3;
        sb.append("Уровень эмоционального интеллекта:\n");
        if (total >= 70) sb.append("Высокий EQ. Вы отлично понимаете себя и окружающих.");
        else if (total >= 40) sb.append("Средний EQ. Вы неплохо справляетесь с эмоциями.");
        else sb.append("EQ требует развития. Эмоции часто кажутся вам загадкой.");

        return sb.toString();
    }

    @Override
    public String getTitle(Intent data) { return "Эмоциональный Интеллект (EQ)"; }

    @Override
    public String getMainResult(Intent intent) {
        Map<String, Integer> data = getChartData(intent);

        // Защита от отсутствия данных
        if (data == null || data.isEmpty()) {
            return "EQ: 0%";
        }

        // Рассчитываем среднее арифметическое всех шкал эмоционального интеллекта
        int sum = 0;
        for (int score : data.values()) {
            sum += score;
        }
        int average = sum / data.size();

        return "EQ: " + average + "%";
    }

    /**
     * Генерирует вопросы для теста EQ.
     * @return Список из 5 вопросов нормальной сложности.
     */
    @Override
    public List<Question> getQuestions() {
        return QuestionGenerator.generateEQ(5, QuestionGenerator.Level.NORMAL);
    }

    /**
     * Подготавливает данные для кругового графика или диаграммы.
     */
    @Override
    public Map<String, Integer> getChartData(Intent data) {
        Map<String, Integer> chartData = new LinkedHashMap<>();

        chartData.put("Самопознание", data.getIntExtra("SELF_AWARE", 0));
        chartData.put("Эмпатия", data.getIntExtra("EMPATHY", 0));
        chartData.put("Социальность", data.getIntExtra("SOCIAL_SKILLS", 0));

        return chartData;
    }

    /**
     * Переводит сырые баллы в проценты и сохраняет их в Intent.
     * @param intent Целевой Intent.
     * @param scores Массив набранных баллов (предполагается 5 вопросов на шкалу, макс 20 баллов).
     */
    @Override
    public void packIntent(Intent intent, int[] scores) {
        // Нормализация: (балл / 20) * 100
        intent.putExtra("SELF_AWARE", (int) ((scores[0] / 20f) * 100));
        intent.putExtra("SOCIAL_SKILLS", (int) ((scores[1] / 20f) * 100));
        intent.putExtra("EMPATHY", (int) ((scores[2] / 20f) * 100));
    }

    // Вспомогательные методы для получения текстов описания уровней

    private String getSelfText(int score) {
        if (score >= 70) return "Вы прекрасно распознаете свои эмоции в моменте и понимаете их истинные причины. Вас сложно выбить из колеи.";
        if (score >= 40) return "Вы понимаете свое настроение, но иногда ваши же чувства могут застать вас врасплох.";
        return "Вам бывает сложно описать словами то, что вы чувствуете, из-за чего эмоции могут накапливаться и вырываться наружу.";
    }

    private String getSocialText(int score) {
        if (score >= 70) return "Вы прирожденный коммуникатор. Легко заводите связи, умеете убеждать и вести людей за собой.";
        if (score >= 40) return "Вы комфортно чувствуете себя в привычной компании, но общение с незнакомцами или сложные переговоры могут отнимать много сил.";
        return "Взаимодействие с людьми дается вам тяжело. Вы предпочитаете одиночество или строго деловое общение.";
    }

    private String getEmpathyText(int score) {
        if (score >= 70) return "Вы очень тонко чувствуете настроение других людей. Друзья часто приходят к вам за поддержкой и утешением.";
        if (score >= 40) return "Вы способны посочувствовать человеку, если он прямо расскажет о своей проблеме, но не всегда считываете невербальные сигналы.";
        return "Вам сложно поставить себя на место другого человека. Чужие переживания часто кажутся вам нелогичными или преувеличенными.";
    }
}
