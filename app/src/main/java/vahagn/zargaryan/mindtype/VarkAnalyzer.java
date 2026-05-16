package vahagn.zargaryan.mindtype;

import android.content.Intent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VarkAnalyzer extends BaseAnalyzer {

    @Override
    public List<Question> getQuestions() {
        return QuestionGenerator.generateVARK(4); // по 4 вопроса на тип = 16 вопросов всего
    }

    @Override
    public void packIntent(Intent intent, int[] scores) {
        // Проверка защиты: если массив пустой или не дошел, пишем нули, чтобы не упасть по IndexOutOfBounds
        if (scores == null || scores.length < 4) {
            intent.putExtra("VARK_V", 0);
            intent.putExtra("VARK_A", 0);
            intent.putExtra("VARK_R", 0);
            intent.putExtra("VARK_K", 0);
            return;
        }

        // Максимальный балл на одну шкалу = 4 вопроса * 4 балла = 16 баллов.
        intent.putExtra("VARK_V", (int) ((scores[0] / 16f) * 100));
        intent.putExtra("VARK_A", (int) ((scores[1] / 16f) * 100));
        intent.putExtra("VARK_R", (int) ((scores[2] / 16f) * 100));
        intent.putExtra("VARK_K", (int) ((scores[3] / 16f) * 100));
        intent.putExtra("test_type", "VARK");
    }

    @Override
    public Map<String, Integer> getChartData(Intent data) {
        // Используем русские ключи без слэшей — теперь Firebase их сохранит без проблем,
        // а SpiderChartView красиво отобразит на осях графика.
        Map<String, Integer> chart = new LinkedHashMap<>();
        chart.put("Визуал", data.getIntExtra("VARK_V", 0));
        chart.put("Аудиал", data.getIntExtra("VARK_A", 0));
        chart.put("Чтение и Письмо", data.getIntExtra("VARK_R", 0));
        chart.put("Кинестетик", data.getIntExtra("VARK_K", 0));
        return chart;
    }

    @Override
    public String getAnalysis(Intent data) {
        Map<String, Integer> results = getChartData(data);

        // Находим доминирующий стиль обучения
        String dominant = "Визуал";
        int maxScore = -1;
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                dominant = entry.getKey();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Ваш основной стиль обучения: **").append(dominant).append("**\n\n");

        // Прямая проверка по русским ключам
        switch (dominant) {
            case "Визуал":
                sb.append("**Визуальный стиль**\n")
                        .append("Вы лучше всего воспринимаете информацию через образы, схемы и пространственное представление.\n\n")
                        .append("— **Советы для учебы:**\n")
                        .append("• Используйте интеллект-карты (Mind Maps) для структурирования тем.\n")
                        .append("• Выделяйте важные моменты разными цветами (кодирование цветом).\n")
                        .append("• Заменяйте длинные тексты на лаконичные блок-схемы.");
                break;

            case "Аудиал":
                sb.append("**Аудиальный стиль**\n")
                        .append("Ваш мозг настроен на восприятие звуков, ритмов и интонаций. Вы лучше запоминаете то, что услышали.\n\n")
                        .append("— **Советы для учебы:**\n")
                        .append("• Записывайте лекции на диктофон и переслушивайте их.\n")
                        .append("• Проговаривайте сложные алгоритмы или правила вслух.\n")
                        .append("• Участвуйте в дискуссиях — в споре вы усваиваете материал быстрее.");
                break;

            case "Чтение и Письмо":
                sb.append("**Текстовый стиль (Чтение / Письмо)**\n")
                        .append("Слова — ваш главный инструмент. Вы предпочитаете списки, заметки и печатный текст.\n\n")
                        .append("— **Советы для учебы:**\n")
                        .append("• Переписывайте важные тезисы своими словами.\n")
                        .append("• Составляйте подробные глоссарии и чек-листы.\n")
                        .append("• Читайте дополнительную литературу: инструкции и документация — ваши лучшие друзья.");
                break;

            case "Кинестетик":
                sb.append("**Кинестетический стиль**\n")
                        .append("Вы познаете мир через действие, практику и реальный опыт. Вам сложно долго сидеть на одном месте.\n\n")
                        .append("— **Советы для учебы:**\n")
                        .append("• Используйте метод «обучение через действие» (Learning by doing).\n")
                        .append("• Делайте частые перерывы на физическую активность во время занятий.\n")
                        .append("• Создавайте реальные прототипы или модели того, что изучаете.");
                break;
        }

        if (hasSecondaryType(results, dominant)) {
            sb.append("\n\n💡 *Замечено:* У вас также сильно развит другой канал восприятия. Вы — мультимодальный ученик, что делает ваше обучение более гибким.");
        }

        return sb.toString();
    }

    private boolean hasSecondaryType(Map<String, Integer> results, String dominant) {
        int max = results.get(dominant);
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            if (!entry.getKey().equals(dominant)) {
                if (max - entry.getValue() < 15) return true;
            }
        }
        return false;
    }
}