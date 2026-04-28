package vahagn.zargaryan.mindtype;

import android.content.Intent;

import java.util.List;

public class EQAnalyzer extends BaseAnalyzer {

    @Override
    public String getAnalysis(Intent data) {
        int self = data.getIntExtra("SELF_AWARE", 0);
        int social = data.getIntExtra("SOCIAL_SKILLS", 0);
        int empathy = data.getIntExtra("EMPATHY", 0);

        StringBuilder sb = new StringBuilder();

        // getLevel вызывается напрямую из BaseAnalyzer!
        sb.append("Самопознание: ").append(self).append("% (").append(getLevel(self)).append(")\n");
        sb.append(getSelfText(self)).append("\n\n");

        sb.append("Социальные навыки: ").append(social).append("% (").append(getLevel(social)).append(")\n");
        sb.append(getSocialText(social)).append("\n\n");

        sb.append("Эмпатия: ").append(empathy).append("% (").append(getLevel(empathy)).append(")\n");
        sb.append(getEmpathyText(empathy)).append("\n\n");

        int total = (self + social + empathy) / 3;
        sb.append("Уровень эмоционального интеллекта:\n");
        if (total >= 70) sb.append("Высокий EQ. Вы отлично понимаете себя и окружающих.");
        else if (total >= 40) sb.append("Средний EQ. Вы неплохо справляетесь с эмоциями.");
        else sb.append("EQ требует развития. Эмоции часто кажутся вам загадкой.");

        return sb.toString();
    }

    @Override
    public List<Question> getQuestions() {
        // Дергаем 5 вопросов нормального уровня
        return QuestionGenerator.generateEQ(5, QuestionGenerator.Level.NORMAL);
    }

    @Override
    public void packIntent(Intent intent, int[] scores) {
        // Нормализуем в проценты (делим на 20 и умножаем на 100)
        intent.putExtra("SELF_AWARE", (int) ((scores[0] / 20f) * 100));
        intent.putExtra("SOCIAL_SKILLS", (int) ((scores[1] / 20f) * 100));
        intent.putExtra("EMPATHY", (int) ((scores[2] / 20f) * 100));
    }

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