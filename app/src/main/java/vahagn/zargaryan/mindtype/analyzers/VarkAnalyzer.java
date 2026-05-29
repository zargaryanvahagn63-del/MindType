package vahagn.zargaryan.mindtype.analyzers;

import android.content.Intent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vahagn.zargaryan.mindtype.tests.Question;
import vahagn.zargaryan.mindtype.tests.QuestionGenerator;

/**
 * Анализатор для теста VARK.
 */
public class VarkAnalyzer extends BaseAnalyzer {

    @Override
    public String getTitle(Intent data) {
        return "Стиль обучения (VARK)";
    }

    @Override
    public String getMainResult(Intent data) {
        Map<String, Integer> results = getChartData(data);
        String dominant = "Не определен";
        int maxScore = -1;
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                dominant = entry.getKey();
            }
        }
        return "Доминирует: " + dominant;
    }

    @Override
    public List<Question> getQuestions() {
        return QuestionGenerator.generateVARK(4);
    }

    @Override
    public void packIntent(Intent intent, int[] scores) {
        if (scores == null || scores.length < 4) return;
        intent.putExtra("VARK_V", (int) ((scores[0] / 16f) * 100));
        intent.putExtra("VARK_A", (int) ((scores[1] / 16f) * 100));
        intent.putExtra("VARK_R", (int) ((scores[2] / 16f) * 100));
        intent.putExtra("VARK_K", (int) ((scores[3] / 16f) * 100));
    }

    @Override
    public Map<String, Integer> getChartData(Intent data) {
        Map<String, Integer> chart = new LinkedHashMap<>();
        chart.put("Визуал", data.getIntExtra("VARK_V", 0));
        chart.put("Аудиал", data.getIntExtra("VARK_A", 0));
        chart.put("Текст", data.getIntExtra("VARK_R", 0));
        chart.put("Кинестетик", data.getIntExtra("VARK_K", 0));
        return chart;
    }

    @Override
    public String getAnalysis(Intent data) {
        String dominant = getMainResult(data).replace("Доминирует: ", "");
        StringBuilder sb = new StringBuilder();
        sb.append("Ваш основной стиль: ").append(dominant).append("\n\n");
        // ... (описание стилей остается прежним)
        return sb.toString();
    }
}
