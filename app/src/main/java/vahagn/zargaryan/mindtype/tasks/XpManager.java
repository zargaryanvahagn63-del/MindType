package vahagn.zargaryan.mindtype.tasks;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
/**
 * Менеджер управления опытом (XP) пользователя.
 * Теперь работает точечно, не затирая другие поля пользователя (currentStep, results и т.д.).
 */
public class XpManager {

    public static final int XP_REWARD_TEST = 500;

    public interface XpCallback {
        void onSuccess(int newXp, boolean leveledUp);
    }

    /**
     * Добавляет опыт и увеличивает счетчик тестов точечно.
     */
    public static void addXp(int amount, XpCallback callback) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        userRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // ПРОВЕРКА: Если данных нет, выходим
                if (mutableData.getValue() == null) return Transaction.success(mutableData);

                // 1. Работаем ТОЛЬКО с полем XP
                Integer currentXp = mutableData.child("xp").getValue(Integer.class);
                if (currentXp == null) currentXp = 0;

                int oldXp = currentXp;
                int newXp = oldXp + amount;

                // Сохраняем новый XP
                mutableData.child("xp").setValue(newXp);

                // 2. Работаем ТОЛЬКО с полем testsCount (если это награда за тест)
                if (amount == XP_REWARD_TEST) {
                    Integer count = mutableData.child("testsCount").getValue(Integer.class);
                    if (count == null) count = 0;
                    mutableData.child("testsCount").setValue(count + 1);
                }

                // Логика уровня остается прежней, но на основе локальных переменных
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (committed && callback != null && snapshot.exists()) {
                    Integer finalXp = snapshot.child("xp").getValue(Integer.class);
                    if (finalXp != null) {
                        boolean leveledUp = checkLevelUp(finalXp - amount, finalXp);
                        callback.onSuccess(finalXp, leveledUp);
                    }
                }
            }
        });
    }

    private static boolean checkLevelUp(int oldXp, int newXp) {
        int[] thresholds = {500, 1500, 3000, 5000};
        for (int t : thresholds) {
            if (oldXp < t && newXp >= t) return true;
        }
        return false;
    }
}