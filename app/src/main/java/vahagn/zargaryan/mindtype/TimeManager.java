package vahagn.zargaryan.mindtype;

public class TimeManager {
    public static String getCurrentDateKey() {
        // Формат 2026-05-11
        return new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
    }
}