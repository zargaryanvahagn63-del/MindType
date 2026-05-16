package vahagn.zargaryan.mindtype;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MoodReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "mood_channel";
    private static final String ACTION_SAVE_MOOD = "vahagn.zargaryan.mindtype.SAVE_MOOD";
    private static final int NOTIFICATION_ID = 5005;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Создаем канал уведомлений (для Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Трекер настроения", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        if ("CHECK_MOOD".equals(action)) {
            // ПОКАЗЫВАЕМ УВЕДОМЛЕНИЕ С КНОПКАМИ-СМАЙЛИКАМИ
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_mood_notification);

            // Настраиваем приветствие в зависимости от времени суток прямо в шторке
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hour >= 6 && hour < 12) remoteViews.setTextViewText(R.id.notif_title, "MindType: Как начинается утро?");
            else if (hour >= 12 && hour < 18) remoteViews.setTextViewText(R.id.notif_title, "MindType: Как проходит день?");
            else remoteViews.setTextViewText(R.id.notif_title, "MindType: Как прошел вечер?");

            // Привязываем клики к каждому смайлику
            int[] btnIds = {R.id.btn_mood_1, R.id.btn_mood_2, R.id.btn_mood_3, R.id.btn_mood_4, R.id.btn_mood_5};
            for (int i = 0; i < btnIds.length; i++) {
                Intent clickIntent = new Intent(context, MoodReceiver.class);
                clickIntent.setAction(ACTION_SAVE_MOOD);
                clickIntent.putExtra("MOOD_VALUE", i + 1); // Записываем оценку от 1 до 5

                PendingIntent pi = PendingIntent.getBroadcast(
                        context,
                        btnIds[i],
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
                );
                remoteViews.setOnClickPendingIntent(btnIds[i], pi);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Поставь сюда свою иконку
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(remoteViews)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if (manager != null) {
                manager.notify(NOTIFICATION_ID, builder.build());
            }

        } else if (ACTION_SAVE_MOOD.equals(action)) {
            // КЛИК ПО СМАЙЛИКУ — СОХРАНЯЕМ В FIREBASE БЕЗ ОТКРЫТИЯ ПРИЛОЖЕНИЯ
            int moodValue = intent.getIntExtra("MOOD_VALUE", 3);
            String uid = FirebaseAuth.getInstance().getUid();

            if (uid != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                String timeSlot;
                if (currentHour >= 6 && currentHour < 12) timeSlot = "morning";
                else if (currentHour >= 12 && currentHour < 18) timeSlot = "afternoon";
                else timeSlot = "evening";

                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(uid)
                        .child("mood_history")
                        .child(currentDate);

                ref.child(timeSlot).setValue(moodValue);
                ref.child("dayOfWeek").setValue(dayOfWeek);

                // Всплывашка, подтверждающая, что всё записано фоном
                Toast.makeText(context.getApplicationContext(), "Настроение сохранено 👍", Toast.LENGTH_SHORT).show();
            }

            // Тушим уведомление после клика
            if (manager != null) {
                manager.cancel(NOTIFICATION_ID);
            }

        } else if ("SHOW_REPORT".equals(action)) {
            // ВОСКРЕСНЫЙ ОТЧЕТ — ТУТ МЫ ОТКРЫВАЕМ ПРИЛОЖЕНИЕ
            Intent activityIntent = new Intent(context, ResultActivity.class);
            activityIntent.putExtra("type", "MOOD_REPORT");
            activityIntent.putExtra("FROM_NOTIFICATION", true);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pi = PendingIntent.getActivity(
                    context,
                    999,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Ваш отчет за неделю готов!")
                    .setContentText("Узнайте, в какие дни вы чаще всего грустили или радовались.")
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            if (manager != null) {
                manager.notify(1010, builder.build());
            }
        }
    }
}