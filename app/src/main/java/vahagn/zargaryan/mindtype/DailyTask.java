package vahagn.zargaryan.mindtype;
public class DailyTask {
    public String id;
    public String title;
    public int xpReward;
    public boolean isCompleted;

    // ОСТАВЬ ТАК: Пустой конструктор без параметров для Firebase
    public DailyTask() {}

    // Конструктор для создания задач в коде
    public DailyTask(String id, String title, int xpReward, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.xpReward = xpReward;
        this.isCompleted = isCompleted;
    }
}