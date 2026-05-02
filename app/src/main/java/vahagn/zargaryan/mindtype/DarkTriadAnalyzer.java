package vahagn.zargaryan.mindtype;

import android.content.Intent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DarkTriadAnalyzer extends BaseAnalyzer {
    @Override
    public String getAnalysis(Intent data) {
        int narc = data.getIntExtra("NARCISSISM", 0);
        int mach = data.getIntExtra("MACHIAVELLIANISM", 0);
        int psy = data.getIntExtra("PSYCHOPATHY", 0);

        StringBuilder sb = new StringBuilder();

        sb.append("Нарциссизм: ").append(narc).append("% (").append(getLevel(narc)).append(")\n");
        sb.append(getNarcText(narc)).append("\n\n");

        sb.append("Макиавеллизм: ").append(mach).append("% (").append(getLevel(mach)).append(")\n");
        sb.append(getMachText(mach)).append("\n");

        sb.append("Психопатия: ").append(psy).append("% (").append(getLevel(psy)).append(")\n");
        sb.append(getPsyText(psy)).append("\n");

        // Общий вывод
        int avg = (narc + mach + psy) / 3;
        sb.append("Общий профиль:\n");
        if (avg > 70) sb.append("У вас ярко выражены черты «Темной триады». Вы склонны к риску и холодному расчету.");
        else if (avg > 40) sb.append("Ваши показатели сбалансированы. Вы умеете постоять за себя, не переходя грани.");
        else sb.append("У вас низкий уровень «темных» черт. Вы искренни и доверчивы.");

        return sb.toString();
    }

    @Override
    public List<Question> getQuestions() {
        return QuestionGenerator.generateDarkTriad(5, QuestionGenerator.Level.NORMAL);
    }

    @Override
    public Map<String, Integer> getChartData(Intent data) {
        // Используем LinkedHashMap, чтобы сохранить порядок осей на графике
        Map<String, Integer> map = new LinkedHashMap<>();

        // Ключ — это подпись на графике, значение — процент, который мы положили в packIntent
        map.put("Нарциссизм", data.getIntExtra("NARCISSISM", 0));
        map.put("Макиавеллизм", data.getIntExtra("MACHIAVELLIANISM", 0));
        map.put("Психопатия", data.getIntExtra("PSYCHOPATHY", 0));

        return map;
    }

    @Override
    public void packIntent(Intent intent, int[] scores) {
        intent.putExtra("NARCISSISM", (int) ((scores[0] / 20f) * 100));
        intent.putExtra("MACHIAVELLIANISM", (int) ((scores[1] / 20f) * 100));
        intent.putExtra("PSYCHOPATHY", (int) ((scores[2] / 20f) * 100));
    }

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