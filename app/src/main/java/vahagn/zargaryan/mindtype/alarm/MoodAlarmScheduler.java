package vahagn.zargaryan.mindtype.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

/**
 * Планировщик уведомлений для трекера настроения.
 * Отвечает за установку точных будильников для утренней, дневной и вечерней проверки настроения,
 * а также для еженедельного отчета.
 *
 * График напоминаний оптимизирован под слоты: 9:00, 14:00 и 20:00.
 */
public class MoodAlarmScheduler {

    // Константы кодов запросов для идентификации будильников
    public static final int REQ_MORNING = 2001;
    public static final int REQ_AFTERNOON = 2002;
    public static final int REQ_EVENING = 2003;
    public static final int REQ_WEEKLY_REPORT = 2004;

    /**
     * Планирует все стандартные уведомления приложения.
     * @param context Контекст приложения.
     */
    public static void scheduleAllAlarms(Context context) {
        // Установка ежедневных проверок (9:00, 14:00, 20:00)
        scheduleNextExactAlarm(context, 9, 0, REQ_MORNING, "CHECK_MOOD");
        scheduleNextExactAlarm(context, 14, 0, REQ_AFTERNOON, "CHECK_MOOD");
        scheduleNextExactAlarm(context, 20, 0, REQ_EVENING, "CHECK_MOOD"); // Изменено на 20:00 по твоему запросу

        // Установка еженедельного отчета (Воскресенье, 22:00)
        scheduleNextWeeklyAlarm(context, 22, 0, REQ_WEEKLY_REPORT, "SHOW_REPORT");
    }

    /**
     * Планирует точный одиночный будильник.
     * Если время на сегодня уже прошло, ставит на завтра.
     */
    public static void scheduleNextExactAlarm(Context context, int hour, int minute, int requestCode, String action) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, MoodReceiver.class);
        intent.setAction(action);
        intent.putExtra("REQ_CODE", requestCode);

        // ДОБАВЛЕНО: Передаем целевой час ("9", "14" или "20").
        // Это нужно, чтобы в MoodReceiver при переназначении будильника мы знали изначальное расписание.
        intent.putExtra("TARGET_HOUR", hour);

        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = getTriggerTime(hour, minute, false);

        // Проверка разрешения на точные будильники (обязательно для Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) {
                // Если разрешения нет, используем безопасный, но менее точный метод
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
                return;
            }
        }

        // Установка точного будильника, который сработает даже в режиме энергосбережения Doze
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
    }

    /**
     * Планирует еженедельный отчет.
     */
    public static void scheduleNextWeeklyAlarm(Context context, int hour, int minute, int requestCode, String action) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        long triggerTime = getTriggerTime(hour, minute, true);

        // Фолбек для Android 12+ без разрешений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            Intent intent = new Intent(context, MoodReceiver.class);
            intent.setAction(action);
            PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
            return;
        }

        Intent intent = new Intent(context, MoodReceiver.class);
        intent.setAction(action);

        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
    }

    /**
     * Вспомогательный метод для расчета времени срабатывания в миллисекундах.
     * @param isWeekly true, если планируется на воскресенье.
     */
    private static long getTriggerTime(int hour, int minute, boolean isWeekly) {
        Calendar c = Calendar.getInstance();

        if (isWeekly) {
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        }

        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        // Если рассчитанное время уже в прошлом, добавляем день или неделю
        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            if (isWeekly) {
                c.add(Calendar.WEEK_OF_YEAR, 1);
            } else {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return c.getTimeInMillis();
    }
}