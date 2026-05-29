package vahagn.zargaryan.mindtype.alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Менеджер времени.
 * Используется для генерации уникальных ключей дат для синхронизации ежедневных задач.
 */
public class TimeManager {
    
    /**
     * Возвращает текущую дату в формате "yyyy-MM-dd".
     * Этот формат удобен для использования в качестве ключа в базе данных Firebase.
     * @return Строка с датой (например, "2024-05-20").
     */
    public static String getCurrentDateKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
