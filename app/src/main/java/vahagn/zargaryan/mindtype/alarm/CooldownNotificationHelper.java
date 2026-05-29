package vahagn.zargaryan.mindtype.alarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.StartActivity;

/**
 * Класс-помощник для создания закрепленного уведомления с таймером обратного отсчета.
 * Оптимизирован под минимальный приоритет (без иконок в строке состояния и без всплывающих баннеров).
 */
public class CooldownNotificationHelper {

    public static final String CHANNEL_ID = "mindtype_cooldown_channel";
    public static final int COOLDOWN_NOTIF_ID = 1001;
    public static final int ALARM_REQUEST_CODE = 2002;

    /**
     * Запускает кулдаун-уведомление с системным таймером и планирует его завершение.
     * @param context Контекст приложения.
     * @param durationMs Длительность блокировки в миллисекундах.
     */
    public static void startCooldownNotification(Context context, long durationMs) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 1. Создаем канал уведомлений с МИНИМАЛЬНОЙ важностью (для Android 8.0+)
        // IMPORTANCE_MIN гарантирует, что уведомление будет скрыто из статус-бара и не покажет баннер
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Восстановление разума",
                    NotificationManager.IMPORTANCE_MIN // Скрывает иконку из статус-бара, убирает всплытие и звук
            );
            channel.setDescription("Тихое уведомление о времени до следующего теста");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Финальная точка времени, когда тест будет доступен
        long targetTimeMs = System.currentTimeMillis() + durationMs;

        // Клик по уведомлению будет открывать приложение
        Intent openIntent = new Intent(context, StartActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 2. Строим уведомление с минимальным приоритетом (для старых версий Android)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("MindType: Идет восстановление...")
                .setContentText("Новый тест станет доступен через:")
                .setSmallIcon(R.drawable.ic_test_result) // Замени на иконку своего проекта
                .setOngoing(true) // ЗАПРЕЩАЕТ стирать уведомление вручную
                .setContentIntent(openPendingIntent)

                // Устанавливаем минимальный приоритет, чтобы уведомление не лезло наверх экрана
                .setPriority(NotificationCompat.PRIORITY_MIN)

                // Системный хронометр на обратный отсчет
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
                .setWhen(targetTimeMs);

        if (notificationManager != null) {
            notificationManager.notify(COOLDOWN_NOTIF_ID, builder.build());
        }

        // 3. Планируем AlarmManager, который сработает точно в момент окончания таймера
        scheduleFinishAlarm(context, targetTimeMs);
    }

    private static void scheduleFinishAlarm(Context context, long triggerTimeMs) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, CooldownReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent);
            }
        }
    }

    /**
     * Позволяет вручную отменить кулдаун-уведомление
     */
    public static void cancelCooldownNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(COOLDOWN_NOTIF_ID);
        }
    }
}