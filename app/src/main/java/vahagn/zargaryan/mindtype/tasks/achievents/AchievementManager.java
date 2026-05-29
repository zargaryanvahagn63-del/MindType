package vahagn.zargaryan.mindtype.tasks.achievents;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vahagn.zargaryan.mindtype.tasks.XpManager;

/**
 * Управляющий класс для системы достижений.
 * Содержит в себе логику проверки условий и автоматической выдачи медалей.
 */
public class AchievementManager {

    private static final String TAG = "AchievementManager";

    public interface OnAchievementUnlockedListener {
        void onUnlocked(Achievement achievement);
    }

    /**
     * Возвращает полный список зарегистрированных в системе ачивок.
     */
    public static List<Achievement> getBaseAchievements() {
        List<Achievement> list = new ArrayList<>();
        list.add(new Achievement("unlocked_mbti", "Первооткрыватель разума", "Сделайте первый шаг — завершите исследование MBTI.", "BRONZE", 100));
        list.add(new Achievement("all_tests_done", "Абсолютный разум", "Пройдите все 6 тестов вашей цепочки самопознания.", "GOLD", 500));
        list.add(new Achievement("all_tests_one_day", "Марафонец разума", "Успейте завершить все исследования в течение 24 часов.", "SILVER", 300));
        return list;
    }

    /**
     * Запускает проверку достижений после прохождения теста.
     */
    public static void checkAndUnlockAchievements(OnAchievementUnlockedListener listener) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                // Чтение данных о завершенных тестах
                boolean mbtiDone = snapshot.child("mbtiDone").getValue(Boolean.class) != null && snapshot.child("mbtiDone").getValue(Boolean.class);
                boolean eqDone = snapshot.child("eqDone").getValue(Boolean.class) != null && snapshot.child("eqDone").getValue(Boolean.class);
                boolean big5Part1 = snapshot.child("big5_part1Done").getValue(Boolean.class) != null && snapshot.child("big5_part1Done").getValue(Boolean.class);
                boolean varkDone = snapshot.child("varkDone").getValue(Boolean.class) != null && snapshot.child("varkDone").getValue(Boolean.class);
                boolean big5Part2 = snapshot.child("big5_part2Done").getValue(Boolean.class) != null && snapshot.child("big5_part2Done").getValue(Boolean.class);
                boolean dark3Done = snapshot.child("dark3Done").getValue(Boolean.class) != null && snapshot.child("dark3Done").getValue(Boolean.class);

                // Список уже полученных ачивок
                DataSnapshot achSnapshot = snapshot.child("achievements");
                Map<String, Boolean> unlockedMap = new HashMap<>();
                if (achSnapshot.exists()) {
                    for (DataSnapshot child : achSnapshot.getChildren()) {
                        unlockedMap.put(child.getKey(), true);
                    }
                }

                // 1. ПРОВЕРКА: Первооткрыватель (MBTI)
                if (mbtiDone && !unlockedMap.containsKey("unlocked_mbti")) {
                    unlock(userRef, "unlocked_mbti", 100, listener);
                }

                // 2. ПРОВЕРКА: Абсолютный разум (Все 6 тестов)
                boolean allDone = mbtiDone && eqDone && big5Part1 && varkDone && big5Part2 && dark3Done;
                if (allDone && !unlockedMap.containsKey("all_tests_done")) {
                    unlock(userRef, "all_tests_done", 500, listener);
                }

                // 3. ПРОВЕРКА: Марафон (Все тесты за 24 часа)
                if (allDone && !unlockedMap.containsKey("all_tests_one_day")) {
                    // Проверяем разницу во времени между первым и последним тестом
                    long lastTestTime = snapshot.child("lastTestTimestamp").getValue(Long.class) != null ?
                            snapshot.child("lastTestTimestamp").getValue(Long.class) : 0;

                    // Так как тесты проходятся строго последовательно, mbti - самый первый, dark3 - последний.
                    // Если разница времени между ними меньше суток, то вызов принят!
                    if (lastTestTime > 0) {
                        long diff = System.currentTimeMillis() - lastTestTime; // Оценочная разница
                        if (diff < 24L * 60L * 60L * 1000L) {
                            unlock(userRef, "all_tests_one_day", 300, listener);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Ошибка проверки достижений: " + error.getMessage());
            }
        });
    }

    private static void unlock(DatabaseReference userRef, String id, int xp, OnAchievementUnlockedListener listener) {
        // Запись в базу о разблокировке
        userRef.child("achievements").child(id).setValue(true);

        // Начисление XP через XPManager
        XpManager.addXp(xp, (newXp, leveledUp) -> {
            // Увеличиваем общий счетчик медалей
            userRef.child("medalsCount").get().addOnSuccessListener(snapshot -> {
                int count = 0;
                if (snapshot.exists() && snapshot.getValue() != null) {
                    count = ((Number) snapshot.getValue()).intValue();
                }
                userRef.child("medalsCount").setValue(count + 1);
            });

            // Уведомляем интерфейс
            if (listener != null) {
                for (Achievement a : getBaseAchievements()) {
                    if (a.getId().equals(id)) {
                        a.setUnlocked(true);
                        listener.onUnlocked(a);
                    }
                }
            }
        });
    }
}