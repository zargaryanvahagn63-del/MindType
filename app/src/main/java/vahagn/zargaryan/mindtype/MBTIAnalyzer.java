package vahagn.zargaryan.mindtype;

import android.content.Intent;

import java.util.List;
import java.util.Random;

public class MBTIAnalyzer extends BaseAnalyzer {

    static Random r = new Random();

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
        int E = data.getIntExtra("E", 0);
        int S = data.getIntExtra("S", 0);
        int T = data.getIntExtra("T", 0);
        int J = data.getIntExtra("J", 0);

        // Используем pick() из BaseAnalyzer
        String desc = descEI(E) + " " + descSN(S) + " " + descTF(T) + " " + descJP(J) + "\n\n" + summary(getTitle(data));

        if (E == 50 || S == 50 || T == 50 || J == 50) {
            desc += "\n\n*Некоторые твои черты находятся в абсолютном балансе.";
        }
        return desc;
    }

    @Override
    public void packIntent(Intent intent, int[] scores) {
        intent.putExtra("E", (int) ((scores[0] / 20f) * 100));
        intent.putExtra("S", (int) ((scores[1] / 20f) * 100));
        intent.putExtra("T", (int) ((scores[2] / 20f) * 100));
        intent.putExtra("J", (int) ((scores[3] / 20f) * 100));
    }

    @Override
    public List<Question> getQuestions() {
        return QuestionGenerator.generateMBTI(5, QuestionGenerator.Level.NORMAL);
    }

    private String descEI(int E) {
        if (E > 50) return pick("Ты получаешь энергию от людей.", "Общение тебя заряжает.");
        else return pick("Ты черпаешь энергию в одиночестве.", "Тебе комфортнее одному.");
    }

    private String descSN(int S) {
        if (S > 50) return pick("Ты опираешься на факты.", "Ты практичен.");
        else return pick("Ты думаешь о возможностях.", "Ты ориентирован на идеи.");
    }

    private String descTF(int T) {
        if (T > 50) return pick("Ты принимаешь решения логикой.", "Рациональность для тебя важна.");
        else return pick("Ты ориентируешься на чувства.", "Эмоции важны для тебя.");
    }

    private String descJP(int J) {
        if (J > 50) return pick("Ты любишь порядок.", "Ты предпочитаешь план.");
        else return pick("Ты гибкий и спонтанный.", "Ты не любишь жесткие рамки.");
    }

    private String summary(String type) {
        switch (type) {
            case "INTJ": return "Стратег. Глубокий аналитик с четким планом на всё.";
            case "INTP": return "Ученый. Постоянный поиск логики и новых идей.";
            case "ENTJ": return "Командир. Решительный лидер, который видит цель.";
            case "ENTP": return "Полемист. Находит выход из любой ситуации через интеллект.";

            case "INFJ": return "Активист. Идеалист с сильным внутренним стержнем.";
            case "INFP": return "Посредник. Добрый, поэтичный и всегда ищет гармонию.";
            case "ENFJ": return "Тренер. Вдохновляющий лидер, который верит в людей.";
            case "ENFP": return "Борец. Энтузиаст, который видит во всем возможности.";

            case "ISTJ": return "Администратор. Практик, на которого всегда можно положиться.";
            case "ISFJ": return "Защитник. Очень преданный и заботливый человек.";
            case "ESTJ": return "Менеджер. Отличный организатор и приверженец традиций.";
            case "ESFJ": return "Консул. Социальный, популярный и всегда готов помочь.";

            case "ISTP": return "Виртуоз. Мастер инструментов и хладнокровный логик.";
            case "ISFP": return "Артист. Творческая натура, живущая в моменте.";
            case "ESTP": return "Делец. Энергичный человек, который любит риск.";
            case "ESFP": return "Развлекатель. Душа компании, спонтанный и яркий.";

            default: return "Твой тип уникален.";
        }
    }
}