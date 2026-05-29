package vahagn.zargaryan.mindtype.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import vahagn.zargaryan.mindtype.R;
import vahagn.zargaryan.mindtype.StartActivity;

/**
 * Приемник, который срабатывает, когда 24-часовой таймер блокировки истек.
 * Удаляет "тихое" уведомление и показывает "активное".
 */
public class CooldownReceiver extends BroadcastReceiver {

    public static final String READY_CHANNEL_ID = "mindtype_ready_channel";
    public static final int READY_NOTIF_ID = 1002;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) return;

        // 1. Стираем старое тихое уведомление с таймером (ID 1001)
        notificationManager.cancel(CooldownNotificationHelper.COOLDOWN_NOTIF_ID);

        // 2. Создаем канал для ГРОМКИХ уведомлений о готовности
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    READY_CHANNEL_ID,
                    "Доступ к тестам",
                    NotificationManager.IMPORTANCE_HIGH // Чтобы всплыло и был звук
            );
            channel.setDescription("Уведомление о том, что блокировка снята");
            notificationManager.createNotificationChannel(channel);
        }

        // Клик по уведомлению открывает приложение
        Intent openIntent = new Intent(context, StartActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                1,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 3. Строим уведомление, которое теперь можно смахнуть и которое привлечет внимание
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, READY_CHANNEL_ID)
                .setContentTitle("MindType: Разум восстановился! 🧠")
                .setContentText("Период ожидания окончен. Новый тест уже ждет тебя!")
                .setSmallIcon(R.drawable.ic_test_result) // Твоя иконка
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Звук и вибрация
                .setAutoCancel(true) // Удаляется после клика
                .setContentIntent(pendingIntent);

        // Показываем финальное уведомление
        notificationManager.notify(READY_NOTIF_ID, builder.build());
    }
}