package vahagn.zargaryan.mindtype;

public class Question {

    public static final int E = 0;
    public static final int A = 1;
    public static final int C = 2;
    public static final int N = 3;
    public static final int O = 4;

    public int trait;
    public String text;
    public boolean reverse;

    public Question(String text, int trait, boolean reverse) {
        this.text = text;
        this.trait = trait;
        this.reverse = reverse;
    }
}