package vahagn.zargaryan.mindtype;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public class MoodAlarmScheduler {

    public static final int REQ_MORNING = 2001;
    public static final int REQ_AFTERNOON = 2002;
    public static final int REQ_EVENING = 2003;
    public static final int REQ_WEEKLY_REPORT = 2004;

    public static void scheduleAllAlarms(Context context) {
        // 1. Утреннее (09:00)
        scheduleRepeatingAlarm(context, 9, 0, REQ_MORNING, "CHECK_MOOD");
        // 2. Дневное (14:00)
        scheduleRepeatingAlarm(context, 14, 0, REQ_AFTERNOON, "CHECK_MOOD");
        // 3. Вечернее (21:00)
        scheduleRepeatingAlarm(context, 21, 0, REQ_EVENING, "CHECK_MOOD");

        // 4. Воскресный отчет (Воскресенье, 22:00)
        scheduleWeeklyAlarm(context, 22, 0, REQ_WEEKLY_REPORT, "SHOW_REPORT");
    }

    private static void scheduleRepeatingAlarm(Context context, int hour, int minute, int requestCode, String action) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MoodReceiver.class);
        intent.setAction(action);

        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }
    }

    private static void scheduleWeeklyAlarm(Context context, int hour, int minute, int requestCode, String action) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MoodReceiver.class);
        intent.setAction(action);

        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.WEEK_OF_YEAR, 1);
        }

        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pi);
        }
    }
}