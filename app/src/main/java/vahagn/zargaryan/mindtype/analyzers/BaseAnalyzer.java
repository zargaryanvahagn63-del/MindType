package vahagn.zargaryan.mindtype.analyzers;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import java.util.Map;
import java.util.Random;

import vahagn.zargaryan.mindtype.tests.Question;

/**
 * Базовый абстрактный класс для всех анализаторов тестов (MBTI, VARK, Dark Triad и т.д.).
 */
public abstract class BaseAnalyzer {

    protected Random r = new Random();

    public abstract List<Question> getQuestions();
    public abstract Map<String, Integer> getChartData(Intent data);
    public abstract String getMainResult(Intent intent);
    public abstract void packIntent(Intent intent, int[] scores);
    public abstract String getAnalysis(Intent data);

    protected String getLevel(int score) {
        if (score >= 70) return "Высокий";
        if (score >= 40) return "Средний";
        return "Низкий";
    }

    /**
     * ИСПРАВЛЕНИЕ: Теперь метод возвращает красивое дефолтное название теста на основе имени класса,
     * если наследник его не переопределил (например, "DarkTriadAnalyzer" -> "Тест: Dark Triad").
     */
    public String getTitle(Intent data) {
        String className = this.getClass().getSimpleName();
        // Убираем слово Analyzer для чистоты
        String cleanName = className.replace("Analyzer", "");
        // Если это внутренний статический класс (например, BigFivePart1Analyzer)
        if (cleanName.contains("$")) {
            cleanName = cleanName.substring(cleanName.lastIndexOf("$") + 1);
        }
        return "Тест: " + cleanName;
    }

    /**
     * Сохраняет результаты теста в Firebase Realtime Database.
     */
    public void saveResultsToFirebase(Map<String, Integer> results) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || results == null || results.isEmpty()) return;

        String folderName = this.getClass().getSimpleName().toLowerCase().replace("analyzer", "Results");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child(folderName);

        ref.setValue(results)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Results saved to " + folderName))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to save results", e));
    }

    protected String pick(String... arr) {
        return arr[(int) (Math.random() * arr.length)];
    }
}