package vahagn.zargaryan.mindtype;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vahagn.zargaryan.mindtype.result.TestResult;
import vahagn.zargaryan.mindtype.tasks.DailyTask;

/**
 * Основная модель данных пользователя для синхронизации с Firebase.
 * Содержит информацию о прогрессе, результатах тестов и ежедневных задачах.
 */
public class User {
    public String username;     // Имя пользователя
    public int xp;              // Текущий опыт
    public int testsCount;      // Общее количество пройденных тестов
    public int currentStep;     // Текущий шаг в обучении или туториале
    public String mbtiType;     // Результат MBTI (например, "INTJ")
    public boolean mbtiDone = false; // Флаг прохождения теста MBTI
    public Map<String, Integer> mbtiResults; // Детальные баллы MBTI по шкалам

    public Map<String, TestResult> allResults = new HashMap<>();

    @Exclude
    public String uid;          // Идентификатор пользователя (не сохраняется в JSON Firebase)

    public String lastUpdateDate; // Дата последнего обновления (для ежедневных задач)
    public List<DailyTask> currentTasks; // Список текущих задач на день

    // Результаты других тестов
    public Map<String, Integer> varkResults; // Результаты VARK
    public Map<String, Integer> eqResults;   // Результаты эмоционального интеллекта
    public Map<String, Integer> darkResults; // Результаты "Темной триады"

    /**
     * Пустой конструктор для Firebase.
     */
    public User() {}

    /**
     * Конструктор для инициализации пользователя.
     */
    public User(String username, int xp, int testsCount, String mbtiType,
                Map<String, Integer> mbtiResults, Map<String, Integer> varkResults) {
        this.username = username;
        this.xp = xp;
        this.testsCount = testsCount;
        this.mbtiType = mbtiType;
        this.mbtiResults = mbtiResults;
        this.varkResults = varkResults;
    }
}