package vahagn.zargaryan.mindtype;

public enum TaskType {
    // Обязательный первый квест
    MBTI("mbti_first", "Пройти тест на MBTI", 150, "MBTI", "#BB86FC"),

    // Обычные тесты
    TEST_EQ("test_eq", "Оценить свой EQ", 100, "EQ", "#BB86FC"),
    TEST_VARK("test_vark", "Ваш стиль обучения", 100, "VARK", "#BB86FC"),
    TEST_BIG5("test_big5", "Пройти Big Five", 150, "BIG5", "#BB86FC"),
    TEST_DARK("test_dark", "Темная Триада", 200, "DARK3", "#CF6679"),

    // Интерактивы
    SHARE_QUOTE("share_quote", "Поделиться цитатой", 50, null, "#FFB74D");

    public final String id;
    public final String title;
    public final int reward;
    public final String testType; // Строка для MainActivity (может быть null)
    public final String colorHex;

    TaskType(String id, String title, int reward, String testType, String colorHex) {
        this.id = id;
        this.title = title;
        this.reward = reward;
        this.testType = testType;
        this.colorHex = colorHex;
    }

    public static TaskType fromId(String id) {
        for (TaskType type : values()) {
            if (type.id.equals(id)) return type;
        }
        return TEST_EQ;
    }
}