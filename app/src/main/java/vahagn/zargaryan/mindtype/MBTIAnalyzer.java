package vahagn.zargaryan.mindtype;

import android.content.Intent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MBTIAnalyzer extends BaseAnalyzer {

    @Override
    public String getTitle(Intent data) {
        int E = data.getIntExtra("E", 0);
        int S = data.getIntExtra("S", 0);
        int T = data.getIntExtra("T", 0);
        int J = data.getIntExtra("J", 0);

        String type = "";
        type += (E >= 50) ? "E" : "I";
        type += (S >= 50) ? "S" : "N";
        type += (T >= 50) ? "T" : "F";
        type += (J >= 50) ? "J" : "P";

        return type; // Возвращаем INTJ, ENFP и т.д.
    }

    @Override
    public String getAnalysis(Intent data) {
        String type = data.getStringExtra("MBTI_TYPE");
        if (type == null) type = "XXXX";

        int e = data.getIntExtra("E", 0);
        int s = data.getIntExtra("S", 0);
        int t = data.getIntExtra("T", 0);
        int j = data.getIntExtra("J", 0);

        StringBuilder sb = new StringBuilder();
        sb.append("Ваш психологический профиль: ").append(type).append("\n\n");

        // Краткие характеристики шкал
        sb.append(getEIDesc(e)).append("\n");
        sb.append(getSNDesc(s)).append("\n");
        sb.append(getTFDesc(t)).append("\n");
        sb.append(getJPDesc(j)).append("\n\n");

        // Глубокий анализ типа
        sb.append("ПОДРОБНЫЙ РАЗБОР:\n");
        sb.append(getDetailedSummary(type));

        return sb.toString();
    }

    private String getDetailedSummary(String type) {
        switch (type) {
            case "INTJ":
                return "• Сильные стороны: Стратегическое мышление, независимость, решительность.\n" +
                        "• Слабые стороны: Излишняя критичность, скрытность.\n" +
                        "• Совет: Старайтесь учитывать чувства других при принятии решений.";
            case "ENFP":
                return "• Сильные стороны: Креативность, энтузиазм, отличные навыки общения.\n" +
                        "• Слабые стороны: Сложность с концентрацией на рутине, гиперчувствительность.\n" +
                        "• Совет: Доводите начатые проекты до конца, прежде чем браться за новые.";
            // Добавь остальные типы по этому шаблону
            default:
                return "Ваш тип сочетает в себе уникальные черты. Вы стремитесь к самопознанию и гармонии.";
        }
    }

    @Override
    public void packIntent(Intent intent, int[] scores) {
        // Умный расчет: находим макс. балл (кол-во вопросов на шкалу * 4 балла)
        // Если у тебя 5 вопросов на шкалу, то maxScore = 20
        float maxScore = 20f;

        int e = (int) ((scores[0] / maxScore) * 100);
        int s = (int) ((scores[1] / maxScore) * 100);
        int t = (int) ((scores[2] / maxScore) * 100);
        int j = (int) ((scores[3] / maxScore) * 100);

        // Ограничиваем от 0 до 100 на всякий случай
        e = Math.max(0, Math.min(100, e));
        s = Math.max(0, Math.min(100, s));
        t = Math.max(0, Math.min(100, t));
        j = Math.max(0, Math.min(100, j));

        intent.putExtra("E", e);
        intent.putExtra("S", s);
        intent.putExtra("T", t);
        intent.putExtra("J", j);

        String typeCode = "";
        typeCode += (e >= 50) ? "E" : "I";
        typeCode += (s >= 50) ? "S" : "N";
        typeCode += (t >= 50) ? "T" : "F";
        typeCode += (j >= 50) ? "J" : "P";

        intent.putExtra("MBTI_TYPE", typeCode);
    }

    @Override
    public List<Question> getQuestions() {
        return QuestionGenerator.generateMBTI(5, QuestionGenerator.Level.NORMAL);
    }

    @Override
    public Map<String, Integer> getChartData(Intent data) {
        Map<String, Integer> chartData = new LinkedHashMap<>();

        // Показываем процент выраженности основных черт E, N, T, J
        chartData.put("Экстраверсия (E)", data.getIntExtra("E", 0));
        chartData.put("Сенсорика (S)", data.getIntExtra("S", 0));
        chartData.put("Логика (T)", data.getIntExtra("T", 0));
        chartData.put("Рациональность (J)", data.getIntExtra("J", 0));

        return chartData;
    }

    private String getEIDesc(int E) {
        if (E > 50) return pick("Ты получаешь энергию от людей.", "Общение тебя заряжает.");
        else return pick("Ты черпаешь энергию в одиночестве.", "Тебе комфортнее одному.");
    }

    private String getSNDesc(int S) {
        if (S > 50) return pick("Ты опираешься на факты.", "Ты практичен.");
        else return pick("Ты думаешь о возможностях.", "Ты ориентирован на идеи.");
    }

    private String getTFDesc(int T) {
        if (T > 50) return pick("Ты принимаешь решения логикой.", "Рациональность для тебя важна.");
        else return pick("Ты ориентируешься на чувства.", "Эмоции важны для тебя.");
    }

    private String getJPDesc(int J) {
        if (J > 50) return pick("Ты любишь порядок.", "Ты предпочитаешь план.");
        else return pick("Ты гибкий и спонтанный.", "Ты не любишь жесткие рамки.");
    }

//    private String getSummary(String type) {
//        switch (type) {
//            case "INTJ": return "Стратег. Глубокий аналитик с четким планом на всё.";
//            case "INTP": return "Ученый. Постоянный поиск логики и новых идей.";
//            case "ENTJ": return "Командир. Решительный лидер, который видит цель.";
//            case "ENTP": return "Полемист. Находит выход из любой ситуации через интеллект.";
//
//            case "INFJ": return "Активист. Идеалист с сильным внутренним стержнем.";
//            case "INFP": return "Посредник. Добрый, поэтичный и всегда ищет гармонию.";
//            case "ENFJ": return "Тренер. Вдохновляющий лидер, который верит в людей.";
//            case "ENFP": return "Борец. Энтузиаст, который видит во всем возможности.";
//
//            case "ISTJ": return "Администратор. Практик, на которого всегда можно положиться.";
//            case "ISFJ": return "Защитник. Очень преданный и заботливый человек.";
//            case "ESTJ": return "Менеджер. Отличный организатор и приверженец традиций.";
//            case "ESFJ": return "Консул. Социальный, популярный и всегда готов помочь.";
//
//            case "ISTP": return "Виртуоз. Мастер инструментов и хладнокровный логик.";
//            case "ISFP": return "Артист. Творческая натура, живущая в моменте.";
//            case "ESTP": return "Делец. Энергичный человек, который любит риск.";
//            case "ESFP": return "Развлекатель. Душа компании, спонтанный и яркий.";
//
//            default: return "Твой тип уникален.";
//        }
//    }
}