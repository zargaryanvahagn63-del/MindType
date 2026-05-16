package vahagn.zargaryan.mindtype;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class XpManager {

    // Константы наград
    public static final int XP_REWARD_TEST = 500;
    public static final int XP_REWARD_AI_QUESTION = 100;

    public interface XpCallback {
        void onSuccess(int newXp, boolean leveledUp);
    }

    public static void addXp(int amount, XpCallback callback) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        // Используем транзакцию, чтобы избежать ошибок при одновременном обновлении
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) return Transaction.success(mutableData);

                int oldXp = user.xp;
                user.xp += amount;

                // Проверяем, изменился ли ранг (например, порог 1500, 3000...)
                boolean leveledUp = checkLevelUp(oldXp, user.xp);

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (committed && callback != null) {
                    User user = snapshot.getValue(User.class);
                    callback.onSuccess(user.xp, true);
                }
            }
        });
    }

    private static boolean checkLevelUp(int oldXp, int newXp) {
        // Простая логика: если перешагнули порог в 1500, 3000 и т.д.
        int[] thresholds = {500, 1500, 3000, 5000};
        for (int t : thresholds) {
            if (oldXp < t && newXp >= t) return true;
        }
        return false;
    }
}