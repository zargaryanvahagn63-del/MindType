package vahagn.zargaryan.mindtype.tests;

/**
 * Модель вопроса для психологических тестов.
 * Хранит текст вопроса, тип психологической черты, к которой он относится,
 * и флаг инвертированного вопроса.
 */
public class Question {
    private final String text;      // Текст вопроса
    private final int trait;         // Индекс психологической черты (шкалы)
    private final boolean isReverse; // Флаг обратного вопроса (где 1 - это максимум черты, а не 5)
    
    public int selectedValue = -1;   // Выбранное значение (от 0 до 4). -1, если не выбрано.
    public boolean isTouched = false; // Станет true, как только пользователь сдвинет SeekBar.
    public boolean showHighlight = false; // Флаг для визуальной подсветки пропущенных вопросов.

    /**
     * Конструктор вопроса.
     * @param text Текст вопроса.
     * @param trait Индекс шкалы.
     * @param isReverse true, если вопрос обратный.
     */
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
