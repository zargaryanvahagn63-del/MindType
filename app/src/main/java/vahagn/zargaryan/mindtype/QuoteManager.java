package vahagn.zargaryan.mindtype;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuoteManager {
    private static final String BASE_URL = "https://api.forismatic.com/";
    private ForismaticApi api;

    public QuoteManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ForismaticApi.class);
    }

    public void fetchDailyQuote(OnQuoteFetched listener) {
        api.getQuote("getQuote", "json", "ru").enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body().quoteText, response.body().quoteAuthor);
                } else {
                    listener.onError("Ошибка сервера");
                }
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface OnQuoteFetched {
        void onSuccess(String text, String author);
        void onError(String error);
    }
}