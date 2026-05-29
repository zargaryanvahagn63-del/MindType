package vahagn.zargaryan.mindtype.leaderboard;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vahagn.zargaryan.mindtype.User;

/**
 * Менеджер таблицы лидеров.
 * Отвечает за загрузку списка всех пользователей из Firebase и их сортировку по количеству опыта.
 */
public class LeaderboardManager {
    private static final String TAG = "LeaderboardDebug";

    /**
     * Интерфейс для получения результатов загрузки таблицы лидеров.
     */
    public interface LeaderboardCallback {
        void onDataLoaded(List<User> list);
        void onError(String error);
    }

    /**
     * Загружает всех пользователей из узла "users", сортирует их по XP и возвращает через callback.
     * @param callback Слушатель завершения загрузки.
     */
    public void fetchTopUsers(LeaderboardCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        Log.d(TAG, "Запрос данных из узла 'users'...");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> list = new ArrayList<>();

                if (!snapshot.exists()) {
                    Log.w(TAG, "Узел 'users' пуст");
                    callback.onDataLoaded(list);
                    return;
                }

                // Перебор всех дочерних узлов (пользователей)
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);

                    if (user != null) {
                        // Сохраняем UID (ключ узла) в объект пользователя
                        user.uid = userSnap.getKey();
                        
                        // Защита от пустых имен
                        if (user.username == null || user.username.isEmpty()) {
                            user.username = "Анонимный пользователь";
                        }
                        list.add(user);
                    }
                }

                // Сортировка списка по убыванию XP (от большего к меньшему)
                Collections.sort(list, (u1, u2) -> Integer.compare(u2.xp, u1.xp));

                Log.d(TAG, "Успешно загружено пользователей для топ-листа: " + list.size());
                callback.onDataLoaded(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Ошибка загрузки из Firebase: " + error.getMessage());
                callback.onError(error.getMessage());
            }
        });
    }
}
