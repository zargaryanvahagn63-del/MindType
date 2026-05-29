package vahagn.zargaryan.mindtype.tasks.quotes;

/**
 * Модель ответа от API Forismatic.
 * Используется GSON для автоматического сопоставления полей JSON с полями класса.
 */
public class QuoteResponse {
    public String quoteText;    // Текст цитаты
    public String quoteAuthor;  // Автор цитаты
    public String senderName;   // Имя отправителя (если есть)
    public String senderLink;   // Ссылка на отправителя
    public String quoteLink;    // Прямая ссылка на цитату на сайте Forismatic
}
