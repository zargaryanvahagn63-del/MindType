package vahagn.zargaryan.mindtype;

import android.content.Intent;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class BaseAnalyzer {

    protected Random r = new Random();

    // Главный метод, который возвращает подробное описание. Его обязаны написать все наследники.
    public abstract List<Question> getQuestions();

    public abstract Map<String, Integer> getChartData(Intent data);

    // Теперь анализатор сам решает, как положить результаты в Intent
    public abstract void packIntent(Intent intent, int[] scores);

    public abstract String getAnalysis(Intent data);

    protected String getLevel(int score) {
        if (score >= 70) return "Высокий";
        if (score >= 40) return "Средний";
        return "Низкий";
    }

    public String getTitle(Intent data) {
        return "";
    }

    protected String pick(String... arr) {
        return arr[(int) (Math.random() * arr.length)];
    }
}