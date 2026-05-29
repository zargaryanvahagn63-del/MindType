package vahagn.zargaryan.mindtype.tasks;

public class DailyTask {
    private final String title;
    private final String description;
    private final boolean isCompleted;
    private boolean isLocked = false;
    private String lockReason = "";

    public DailyTask() {
        this.title = "";
        this.description = "";
        this.isCompleted = false;
    }

    public DailyTask(String title, String description, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    public void setLocked(boolean locked, String reason) {
        this.isLocked = locked;
        this.lockReason = reason;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getLockReason() {
        return lockReason;
    }
}