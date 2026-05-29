package vahagn.zargaryan.mindtype.tasks.quotes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Менеджер цитат.
 * Отвечает за получение мотивационных цитат из внешнего API (Forismatic)
 * или из локального хранилища в случае отсутствия интернета.
 */
public class QuoteManager {

    private static final String BASE_URL = "http://api.forismatic.com/";
    private ForismaticApi api;
    private final Random random = new Random();

    // Локальный список цитат для работы в оффлайн-режиме
    private final List<LocalQuote> localQuotes = new ArrayList<>();

    /**
     * Вспомогательный класс для хранения локальной цитаты.
     */
    public static class LocalQuote {
        public String text;
        public String author;

        public LocalQuote(String text, String author) {
            this.text = text;
            this.author = author;
        }
    }

    public QuoteManager() {
        initLocalQuotes(); // Инициализация оффлайн-базы

        // Настройка GSON с разрешением нестрогого соответствия JSON (lenient)
        Gson lenientGson = new GsonBuilder()
                .setLenient()
                .create();

        // Настройка HTTP-клиента с таймаутами
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        // Инициализация Retrofit для работы с API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(lenientGson))
                .build();

        api = retrofit.create(ForismaticApi.class);
    }

    /**
     * Заполняет локальный список цитат известными высказываниями о психологии и саморазвитии.
     */
    private void initLocalQuotes() {
        localQuotes.add(new LocalQuote("Единственный человек, с которым вы должны сравнивать себя, — это вы в прошлом.", "Зигмунд Фрейд"));
        localQuotes.add(new LocalQuote("Всё, что раздражает нас в других, может привести к пониманию себя.", "Карл Юнг"));
        localQuotes.add(new LocalQuote("Смысл жизни не в том, чтобы найти себя. Смысл жизни в том, чтобы создать себя.", "Джордж Бернард Шоу"));
        localQuotes.add(new LocalQuote("Твоё видение станет ясным только тогда, когда ты сможешь заглянуть в своё собственное сердце.", "Карл Юнг"));
        localQuotes.add(new LocalQuote("Эмоции, которые не нашли выхода, никогда не умирают. Они просто похоронены заживо.", "Зигмунд Фрейд"));
        localQuotes.add(new LocalQuote("Мы то, что мы делаем систематически. Следовательно, превосходство — это привычка.", "Аристотель"));
        localQuotes.add(new LocalQuote("Дисциплина — это решение делать то, чего очень не хочется, чтобы достичь того, чего хочется.", "Джон Максвелл"));
        localQuotes.add(new LocalQuote("То, что нас не убивает, делает нас сильнее.", "Фридрих Ницше"));
        localQuotes.add(new LocalQuote("Препятствие — это и есть путь.", "Марк Аврелий"));
        localQuotes.add(new LocalQuote("Не так важны события сами по себе, как наше отношение к ним.", "Эпиктет"));
        localQuotes.add(new LocalQuote("Мы не можем направлять ветер, но можем настроить паруса.", "Сенека"));
        localQuotes.add(new LocalQuote("Успех — это способность идти от поражения к поражению, не теряя энтузиазма.", "Уинстон Черчилль"));
        localQuotes.add(new LocalQuote("Лучший способ предсказать будущее — создать его.", "Питер Друкер"));
        localQuotes.add(new LocalQuote("Сложные времена рождают сильных людей.", "Платон"));
        localQuotes.add(new LocalQuote("Ошибки — это знаки препинания жизни, без которых, как и в тексте, не будет смысла.", "Харуки Мураками"));
    }

    /**
     * Возвращает случайную цитату из локального списка.
     */
    private LocalQuote getRandomLocalQuote() {
        int index = random.nextInt(localQuotes.size());
        return localQuotes.get(index);
    }

    /**
     * Запрашивает случайную цитату из интернета.
     * При ошибке или отсутствии связи возвращает локальную цитату.
     */
    public void fetchDailyQuote(OnQuoteFetched listener) {
        int randomKey = random.nextInt(999999) + 1;

        api.getQuote("getQuote", "json", "ru", randomKey).enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QuoteResponse body = response.body();
                    String text = (body.quoteText != null) ? body.quoteText : "";
                    String author = (body.quoteAuthor != null) ? body.quoteAuthor : "";

                    if (text.isEmpty()) {
                        // Фолбек на локальную базу при пустом ответе
                        LocalQuote fallback = getRandomLocalQuote();
                        listener.onSuccess(fallback.text, fallback.author);
                    } else {
                        listener.onSuccess(text, author);
                    }
                } else {
                    // Ошибка сервера
                    LocalQuote fallback = getRandomLocalQuote();
                    listener.onSuccess(fallback.text, fallback.author);
                }
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                // Отсутствие интернета или таймаут
                LocalQuote fallback = getRandomLocalQuote();
                listener.onSuccess(fallback.text, fallback.author);
            }
        });
    }

    /**
     * Интерфейс для получения результата загрузки цитаты.
     */
    public interface OnQuoteFetched {
        void onSuccess(String text, String author);
        void onError(String error);
    }
}
