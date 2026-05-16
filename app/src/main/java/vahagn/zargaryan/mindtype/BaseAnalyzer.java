package vahagn.zargaryan.mindtype;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void saveResultsToFirebase(Map<String, Integer> results) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || results == null || results.isEmpty()) return;

        // Автоматически определяем имя папки (varkResults, mbtiResults и т.д.)
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