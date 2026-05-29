package vahagn.zargaryan.mindtype.tasks.achievents;

/**
 * Модель данных достижения (ачивки).
 * Используется для отображения в RecyclerView внутри TasksFragment или ProfileFragment.
 */
public class Achievement {
    private String id;
    private String title;
    private String description;
    private String medalType; // "GOLD", "SILVER", "BRONZE"
    private int xpReward;
    private boolean isUnlocked;

    // Конструктор по умолчанию для Firebase
    public Achievement() {}

    public Achievement(String id, String title, String description, String medalType, int xpReward) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.medalType = medalType;
        this.xpReward = xpReward;
        this.isUnlocked = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMedalType() { return medalType; }
    public void setMedalType(String medalType) { this.medalType = medalType; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}