package vahagn.zargaryan.mindtype;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DailyTasksManager {

    public interface OnTasksReadyListener {
        void onReady(List<DailyTask> tasks);
    }

    public interface OnTaskCompletedListener {
        void onCompleted(int reward, boolean leveledUp);
    }

    // Проверка и загрузка задач (вызывать в TasksFragment)
    public static void checkAndResetTasks(OnTasksReadyListener listener) {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.get().addOnSuccessListener(snapshot -> {
            User user = snapshot.getValue(User.class);
            if (user == null) return;

            String today = TimeManager.getCurrentDateKey();

            // Если MBTI не пройден — это единственная задача
            if (!user.mbtiDone) {
                List<DailyTask> mandatory = new ArrayList<>();
                mandatory.add(new DailyTask(TaskType.MBTI.id, TaskType.MBTI.title, TaskType.MBTI.reward, false));
                listener.onReady(mandatory);
                return;
            }

            // Если новый день — генерируем 3 новых
            if (user.lastUpdateDate == null || !user.lastUpdateDate.equals(today)) {
                generateNewTasks(userRef, today, listener);
            } else {
                // Старый день — берем из базы
                listener.onReady(user.currentTasks);
            }
        });
    }

    private static void generateNewTasks(DatabaseReference ref, String date, OnTasksReadyListener listener) {
        List<TaskType> pool = new ArrayList<>(Arrays.asList(TaskType.values()));
        pool.remove(TaskType.MBTI);
        Collections.shuffle(pool);

        List<DailyTask> newTasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TaskType type = pool.get(i);
            newTasks.add(new DailyTask(type.id, type.title, type.reward, false));
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("lastUpdateDate", date);
        updates.put("currentTasks", newTasks);
        ref.updateChildren(updates).addOnSuccessListener(v -> listener.onReady(newTasks));
    }

    // Завершение задачи (вызывать в ResultActivity или при шеринге)
    public static void completeTask(String taskId, OnTaskCompletedListener listener) {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.get().addOnSuccessListener(snapshot -> {
            User user = snapshot.getValue(User.class);
            if (user == null || user.currentTasks == null) return;

            boolean changed = false;
            int reward = 0;

            for (DailyTask task : user.currentTasks) {
                if (task.id.equals(taskId) && !task.isCompleted) {
                    task.isCompleted = true;
                    reward = task.xpReward;
                    changed = true;
                    break;
                }
            }

            if (changed) {
                int finalReward = reward;
                userRef.child("currentTasks").setValue(user.currentTasks).addOnSuccessListener(v -> {
                    if (taskId.equals(TaskType.MBTI.id)) userRef.child("mbtiDone").setValue(true);
                    XpManager.addXp(finalReward, (newXp, leveledUp) -> {
                        if (listener != null) listener.onCompleted(finalReward, leveledUp);
                    });
                });
            }
        });
    }
}