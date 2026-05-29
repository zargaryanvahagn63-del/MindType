package vahagn.zargaryan.mindtype.result;

import java.io.Serializable;
import java.util.Map;

/**
 * Модель данных для сохранения результатов пройденного теста.
 * Содержит информацию о названии теста, кратком итоге (статах),
 * текстовом анализе и данных для графика.
 */
public class TestResult implements Serializable {
    public String testName;       // Название теста (например, "Темная триада")
    public String resultHeadline; // Краткий итог для списка (например, "Темность: 45%")
    public String analysisText;   // Подробный текстовый результат анализа
    public Map<String, Integer> chartData; // Данные для построения диаграммы
    public long timestamp;        // Метка времени прохождения теста

    /**
     * Пустой конструктор для Firebase.
     */
    public TestResult() {}

    /**
     * Конструктор для создания объекта результата с учетом RPG-статов.
     * @param testName Название теста.
     * @param resultHeadline Краткий результат (стат).
     * @param analysisText Текст анализа.
     * @param chartData Данные для графика.
     */
    public TestResult(String testName, String resultHeadline, String analysisText, Map<String, Integer> chartData) {
        this.testName = testName;
        this.resultHeadline = resultHeadline;
        this.analysisText = analysisText;
        this.chartData = chartData;
        this.timestamp = System.currentTimeMillis(); // Время устанавливается автоматически
    }
}