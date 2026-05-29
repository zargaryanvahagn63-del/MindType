package vahagn.zargaryan.mindtype.tasks;

/**
 * Перечисление типов заданий в приложении.
 * Определяет правила доступности заданий в зависимости от прогресса пользователя (currentStep).
 */
public enum TaskType {
    
    // 1. ОБЯЗАТЕЛЬНЫЙ ПЕРВЫЙ КВЕСТ: Тест MBTI. Доступен сразу (minStep = 0).
    MBTI("mbti_first", "Пройти тест на MBTI", 150, "MBTI", "#BB86FC", 0),

    // 2. ЭМОЦИОНАЛЬНЫЙ ИНТЕЛЛЕКТ: Открывается после первого шага.
    TEST_EQ("test_eq", "Оценить свой EQ", 100, "EQ", "#BB86FC", 1),

    // 3. БОЛЬШАЯ ПЯТЕРКА: Сложное задание, разделенное на этапы.
    TEST_BIG5("test_big5", "Пройти Big Five", 150, "BIG5", "#BB86FC", 2),

    // 4. VARK: Определение стиля обучения.
    TEST_VARK("test_vark", "Ваш стиль обучения", 100, "VARK", "#BB86FC", 3),

    // 5. ТЕМНАЯ ТРИАДА: Исследование темных сторон личности.
    TEST_DARK("test_dark", "Темная Триада", 200, "DARK3", "#CF6679", 5),

    // 6. ИНТЕРАКТИВЫ: Социальные действия, доступны всегда.
    SHARE_QUOTE("share_quote", "Поделиться цитатой", 50, null, "#FFB74D", 0);

    public final String id;        // Уникальный ID задания
    public final String title;     // Заголовок для отображения
    public final int reward;       // Награда в XP
    public final String testType;  // Соответствующий ключ типа теста
    public final String colorHex;  // Цвет карточки в UI
    public final int minStep;      // Минимальный шаг прогресса для разблокировки

    TaskType(String id, String title, int reward, String testType, String colorHex, int minStep) {
        this.id = id;
        this.title = title;
        this.reward = reward;
        this.testType = testType;
        this.colorHex = colorHex;
        this.minStep = minStep;
    }

    /**
     * Проверяет, доступно ли задание пользователю на текущем шаге.
     * @param currentStep Текущий прогресс пользователя из базы данных.
     * @return true, если задание можно запустить.
     */
    public boolean isAvailable(int currentStep) {
        // Специальная логика для Big Five (доступен на 2-м шаге и позже на 4-м)
        if (this == TEST_BIG5) {
            return currentStep == 2 || currentStep >= 4;
        }
        return currentStep >= this.minStep;
    }

    /**
     * Возвращает актуальный тип теста с учетом прогресса.
     * Используется для запуска разных частей одного и того же теста.
     */
    public String getActualTestType(int currentStep) {
        if (this == TEST_BIG5) {
            if (currentStep == 2) return "BIG5_PART1";
            if (currentStep >= 4) return "BIG5_PART2";
        }
        return this.testType;
    }

    /**
     * Находит тип задания по его строковому идентификатору.
     */
    public static TaskType fromId(String id) {
        for (TaskType type : values()) {
            if (type.id.equals(id)) return type;
        }
        return SHARE_QUOTE; // Дефолтное значение для предотвращения NPE
    }
}
