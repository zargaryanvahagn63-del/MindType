package vahagn.zargaryan.mindtype;

import java.util.List;
import java.util.Map;

public class User {
    public String username;
    public int xp;
    public int testsCount;
    public String mbtiType; // Для хранения строки типа "INTJ"
    public boolean mbtiDone = false;

    // ВОТ ЭТОГО ПОЛЯ НЕ ХВАТАЛО! Текущие результаты шкал MBTI (E, S, T, J в процентах)
    public Map<String, Integer> mbtiResults;

    public String lastUpdateDate;
    public List<DailyTask> currentTasks;

    // Результаты VARK (Visual, Auditory, Read/Write, Kinesthetic)
    public Map<String, Integer> varkResults;

    // Пустой конструктор обязателен для Firebase!
    public User() {}

    // Обновленный конструктор (если ты создаешь пользователя сразу со всеми результатами)
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