package vahagn.zargaryan.mindtype;

import android.content.Intent;
import androidx.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MBTIAnalyzer extends BaseAnalyzer {

    @Override
    public String getTitle(Intent data) {
        if (data == null) return "XXXX";

        int E = data.getIntExtra("E", 0);
        int S = data.getIntExtra("S", 0);
        int T = data.getIntExtra("T", 0);
        int J = data.getIntExtra("J", 0);

        String type = "";
        type += (E >= 50) ? "E" : "I";
        type += (S >= 50) ? "S" : "N";
        type += (T >= 50) ? "T" : "F";
        type += (J >= 50) ? "J" : "P";

        return type;
    }

    public String getTypeName(String type) {
        if (type == null) return "Исследователь";

        switch (type.toUpperCase()) {
            case "INTJ": return "Стратег";
            case "INTP": return "Ученый";
            case "ENTJ": return "Командир";
            case "ENTP": return "Полемист";
            case "INFJ": return "Активист";
            case "INFP": return "Посредник";
            case "ENFJ": return "Тренер";
            case "ENFP": return "Борец";
            case "ISTJ": return "Администратор";
            case "ISFJ": return "Защитник";
            case "ESTJ": return "Менеджер";
            case "ESFJ": return "Консул";
            case "ISTP": return "Виртуоз";
            case "ISFP": return "Артист";
            case "ESTP": return "Делец";
            case "ESFP": return "Развлекатель";
            default: return "Исследователь";
        }
    }

    @Override
    public String getAnalysis(Intent data) {
        if (data == null) return "Информация недоступна";

        String type = data.getStringExtra("MBTI_TYPE");
        if (type == null) type = getTitle(data);

        return getAnalysisByCode(type, data);
    }

    public String getAnalysisByCode(String type, @Nullable Intent data) {
        if (type == null || type.equals("XXXX") || type.isEmpty()) return "Пройдите тест, чтобы получить результат";

        StringBuilder sb = new StringBuilder();
        sb.append("Ваш психологический профиль: ").append(type).append("\n\n");

        if (data != null) {
            int e = data.getIntExtra("E", 0);
            int s = data.getIntExtra("S", 0);
            int t = data.getIntExtra("T", 0);
            int j = data.getIntExtra("J", 0);
            sb.append(getEIDesc(e)).append("\n");
            sb.append(getSNDesc(s)).append("\n");
            sb.append(getTFDesc(t)).append("\n");
            sb.append(getJPDesc(j)).append("\n\n");
        }

        sb.append("ПОДРОБНЫЙ РАЗБОР:\n");
        sb.append(getDetailedSummary(type));

        return sb.toString();
    }

    public String getDetailedSummary(String type) {
        if (type == null) return "";
        switch (type.toUpperCase()) {
            case "INTJ":
                return "• Сильные стороны: Стратегическое мышление, независимость, железная логика.\n" +
                        "• Слабые стороны: Излишняя критичность, эмоциональная скрытность, перфекционизм.\n" +
                        "• Совет: Не забывайте, что человеческий фактор важен не меньше, чем идеальный план.";
            case "INTP":
                return "• Сильные стороны: Нестандартный подход, объективность, открытость новому.\n" +
                        "• Слабые стороны: Рассеянность, склонность к долгим сомнениям, оторванность от быта.\n" +
                        "• Совет: Старайтесь чаще переходить от блестящих теорий к практическим действиям.";
            case "ENTJ":
                return "• Сильные стороны: Уверенность, эффективность, выдающиеся лидерские качества.\n" +
                        "• Слабые стороны: Упрямство, нетерпимость к чужим ошибкам, жесткость.\n" +
                        "• Совет: Учитесь делегировать и прислушиваться к эмоциям команды, а не только к цифрам.";
            case "ENTP":
                return "• Сильные стороны: Интеллектуальная смелость, харизма, быстрая генерация идей.\n" +
                        "• Слабые стороны: Быстрая потеря интереса, любовь к спорам ради спора.\n" +
                        "• Совет: Сфокусируйтесь на доведении начатых проектов до конца, прежде чем браться за новые.";
            case "INFJ":
                return "• Сильные стороны: Глубокая эмпатия, принципиальность, альтруизм.\n" +
                        "• Слабые стороны: Острый перфекционизм, уязвимость к выгоранию, скрытность.\n" +
                        "• Совет: Заботьтесь о себе так же сильно, как вы заботитесь об окружающих.";
            case "INFP":
                return "• Сильные стороны: Искренняя доброта, креативность, верность своим идеалам.\n" +
                        "• Слабые стороны: Излишняя самокритичность, оторванность от суровой реальности.\n" +
                        "• Совет: Ставьте перед собой небольшие практические цели, чтобы не утонуть в мечтах.";
            case "ENFJ":
                return "• Сильные стороны: Природное обаяние, умение вдохновлять, надежность.\n" +
                        "• Слабые стороны: Склонность брать на себя чужие проблемы, зависимость от одобрения.\n" +
                        "• Совет: Не пытайтесь спасти всех. Установите четкие личные границы.";
            case "ENFP":
                return "• Сильные стороны: Неиссякаемый энтузиазм, коммуникабельность, любознательность.\n" +
                        "• Слабые стороны: Неорганизованность, трудности с рутинными задачами.\n" +
                        "• Совет: Дисциплина — это не ограничение свободы, а инструмент для достижения ваших целей.";
            case "ISTJ":
                return "• Сильные стороны: Исключительная ответственность, честность, практичность.\n" +
                        "• Слабые стороны: Упрямство, нелюбовь к внезапным переменам, прямолинейность.\n" +
                        "• Совет: Позвольте себе иногда отклоняться от правил. Гибкость — это тоже навык.";
            case "ISFJ":
                return "• Сильные стороны: Преданность, заботливость, внимание к деталям.\n" +
                        "• Слабые стороны: Неумение говорить «нет», подавление собственных эмоций.\n" +
                        "• Совет: Отстаивайте свои интересы. Ваше благополучие не менее важно, чем комфорт других.";
            case "ESTJ":
                return "• Сильные стороны: Отличная организованность, реализм, целеустремленность.\n" +
                        "• Слабые стороны: Жесткость, требовательность, негибкость в нестандартных ситуациях.\n" +
                        "• Совет: Дайте людям право на ошибку. Иногда лучший результат рождается в хаосе.";
            case "ESFJ":
                return "• Сильные стороны: Отзывчивость, развитое чувство долга, социальная активность.\n" +
                        "• Слабые стороны: Сильная зависимость от чужого мнения, уязвимость к критике.\n" +
                        "• Совет: Учитесь слушать свой внутренний голос, а не только ожидания общества.";
            case "ISTP":
                return "• Сильные стороны: Хладнокровие в кризисных ситуациях, практический ум, спонтанность.\n" +
                        "• Слабые стороны: Эмоциональная закрытость, склонность к неоправданному риску.\n" +
                        "• Совет: Старайтесь чаще делиться своими мыслями с близкими, они не умеют читать мысли.";
            case "ISFP":
                return "• Сильные стороны: Тонкое чувство прекрасного, эмпатия, гибкость.\n" +
                        "• Слабые стороны: Избегание конфликтов в ущерб себе, проблемы с долгосрочным планированием.\n" +
                        "• Совет: Учитесь смотреть на несколько шагов вперед, особенно в финансовых и рабочих вопросах.";
            case "ESTP":
                return "• Сильные стороны: Энергичность, смелость, умение жить «здесь и сейчас».\n" +
                        "• Слабые стороны: Импульсивность, игнорирование долгосрочных последствий, нетерпеливость.\n" +
                        "• Совет: Перед важным решением возьмите паузу на 10 минут, чтобы оценить все риски.";
            case "ESFP":
                return "• Сильные стороны: Оптимизм, блестящие навыки общения, эстетический вкус.\n" +
                        "• Слабые стороны: Избегание серьезных проблем, расфокусировка внимания.\n" +
                        "• Совет: Трудности не исчезнут, если их игнорировать. Решайте сложные задачи первыми.";
            default:
                return "Ваш профиль сочетает в себе множество уникальных черт. Продолжайте исследовать себя, чтобы раскрыть свой истинный потенциал.";
        }
    }

    @Override
    public void packIntent(Intent intent, int[] scores) {
        float questionsPerTrait = 5f;
        float maxScore = questionsPerTrait * 4f;

        int e = (int) ((scores[0] / maxScore) * 100);
        int s = (int) ((scores[1] / maxScore) * 100);
        int t = (int) ((scores[2] / maxScore) * 100);
        int j = (int) ((scores[3] / maxScore) * 100);

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
        if (data != null) {
            chartData.put("Экстраверсия (E)", data.getIntExtra("E", 0));
            chartData.put("Сенсорика (S)", data.getIntExtra("S", 0));
            chartData.put("Логика (T)", data.getIntExtra("T", 0));
            chartData.put("Рациональность (J)", data.getIntExtra("J", 0));
        }
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
}