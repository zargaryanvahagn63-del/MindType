package vahagn.zargaryan.mindtype;

public class Question {
    private String text;
    private int trait;
    private boolean isReverse;

    // Конструктор
    public Question(String text, int trait, boolean isReverse) {
        this.text = text;
        this.trait = trait;
        this.isReverse = isReverse;
    }

    public String getText() {
        return text;
    }

    public int getTrait() {
        return trait;
    }

    public boolean isReverse() {
        return isReverse;
    }
}