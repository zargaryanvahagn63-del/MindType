package vahagn.zargaryan.mindtype.tasks.quotes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Интерфейс для взаимодействия с API Forismatic через Retrofit.
 * Используется для получения случайных цитат.
 */
public interface ForismaticApi {
    /**
     * Выполняет GET-запрос к API для получения цитаты.
     * @param method Метод API (например, "getQuote").
     * @param format Формат ответа (например, "json").
     * @param lang Язык цитаты ("ru" или "en").
     * @param key Случайное число для получения разных цитат.
     * @return Объект Call для выполнения асинхронного запроса.
     */
    @GET("api/1.0/")
    Call<QuoteResponse> getQuote(
            @Query("method") String method,
            @Query("format") String format,
            @Query("lang") String lang,
            @Query("key") int key
    );
}
