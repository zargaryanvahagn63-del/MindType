package vahagn.zargaryan.mindtype.journal;

public class JournalEntry {
    public String text;
    public double moodScore;
    public long timestamp;
    public String dateKey; // yyyy-MM-dd

    public JournalEntry() {} // Нужно для Firebase

    public JournalEntry(String text, double moodScore, long timestamp, String dateKey) {
        this.text = text;
        this.moodScore = moodScore;
        this.timestamp = timestamp;
        this.dateKey = dateKey;
    }
}