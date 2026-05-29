package vahagn.zargaryan.mindtype.alarm;

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

import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.result.ResultActivity;

/**
 * Приемник широковещательных сообщений (BroadcastReceiver) для системы отслеживания настроения.
 * Обрабатывает будильники для проверки настроения, клики по кнопкам в уведомлении,
 * показ еженедельных отчетов и восстановление расписания после перезагрузки.
 */
public class MoodReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "mood_channel"; // ID канала уведомлений
    private static final String ACTION_SAVE_MOOD = "vahagn.zargaryan.mindtype.SAVE_MOOD"; // Экшен сохранения выбора
    private static final int NOTIFICATION_ID = 5005; // Уникальный ID уведомления

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        // 1. ВОССТАНОВЛЕНИЕ ПОСЛЕ ПЕРЕЗАГРУЗКИ
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Если телефон включился, заново запускаем весь цикл уведомлений
            MoodAlarmScheduler.scheduleAllAlarms(context);
            return;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Создаем канал уведомлений (обязательно для Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Трекер настроения", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        // 2. ОБРАБОТКА СИГНАЛА "ПОРА ПРОВЕРИТЬ НАСТРОЕНИЕ"
        switch (action) {
            case "CHECK_MOOD": {
                // ИЗВЛЕКАЕМ ДАННЫЕ ИЗ ИНТЕНТА
                int reqCode = intent.getIntExtra("REQ_CODE", MoodAlarmScheduler.REQ_MORNING);
                int targetHour = intent.getIntExtra("TARGET_HOUR", 9); // На какой час был запланирован этот будильник


                // ПЕРЕНАЗНАЧАЕМ БУДИЛЬНИК НА ЗАВТРА
                // (именно targetHour, чтобы не было смещения времени из-за задержки системы)
                MoodAlarmScheduler.scheduleNextExactAlarm(context, targetHour, 0, reqCode, "CHECK_MOOD");

                // Создаем кастомный вид уведомления
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_mood_notification);

                // Динамический заголовок под твой новый график (9:00, 14:00, 20:00)
                if (targetHour == 9) {
                    remoteViews.setTextViewText(R.id.notif_title, "MindType: Как начинается утро?");
                } else if (targetHour == 14) {
                    remoteViews.setTextViewText(R.id.notif_title, "MindType: Как проходит день?");
                } else {
                    remoteViews.setTextViewText(R.id.notif_title, "MindType: Как прошел вечер?");
                }

                // Настройка кликов для смайликов
                int[] btnIds = {R.id.btn_mood_1, R.id.btn_mood_2, R.id.btn_mood_3, R.id.btn_mood_4, R.id.btn_mood_5};
                for (int i = 0; i < btnIds.length; i++) {
                    Intent clickIntent = new Intent(context, MoodReceiver.class);
                    clickIntent.setAction(ACTION_SAVE_MOOD);
                    clickIntent.putExtra("MOOD_VALUE", i + 1);

                    PendingIntent pi = PendingIntent.getBroadcast(
                            context,
                            btnIds[i],
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );
                    remoteViews.setOnClickPendingIntent(btnIds[i], pi);
                }

                // Сборка уведомления
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground) // Замени на свой ic_notification
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(remoteViews)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                if (manager != null) {
                    manager.notify(NOTIFICATION_ID, builder.build());
                }

                // 3. ОБРАБОТКА ВЫБОРА СМАЙЛИКА
                break;
            }
            case ACTION_SAVE_MOOD:
                int moodValue = intent.getIntExtra("MOOD_VALUE", 3);
                String uid = FirebaseAuth.getInstance().getUid();

                if (uid != null) {
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    // Определяем слот на основе текущего времени (для записи в Firebase)
                    Calendar calendar = Calendar.getInstance();
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    String timeSlot;
                    if (currentHour < 12) timeSlot = "morning";
                    else if (currentHour < 17) timeSlot = "afternoon";
                    else timeSlot = "evening";

                    DatabaseReference ref = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(uid)
                            .child("mood_history")
                            .child(currentDate);

                    ref.child(timeSlot).setValue(moodValue);
                    ref.child("dayOfWeek").setValue(dayOfWeek);

                    Toast.makeText(context, "Настроение сохранено 👍", Toast.LENGTH_SHORT).show();
                }

                if (manager != null) {
                    manager.cancel(NOTIFICATION_ID);
                }

                // 4. ЕЖЕНЕДЕЛЬНЫЙ ОТЧЕТ
                break;
            case "SHOW_REPORT": {
                MoodAlarmScheduler.scheduleNextWeeklyAlarm(context, 22, 0, MoodAlarmScheduler.REQ_WEEKLY_REPORT, "SHOW_REPORT");

                Intent activityIntent = new Intent(context, ResultActivity.class);
                activityIntent.putExtra("type", "MOOD_REPORT");
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pi = PendingIntent.getActivity(
                        context, 999, activityIntent,
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
                break;
            }
        }
    }
}