package vahagn.zargaryan.mindtype;

public class Question {
    private final String text;
    private final int trait;
    private final boolean isReverse;

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