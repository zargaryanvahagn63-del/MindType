package vahagn.zargaryan.mindtype;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForismaticApi {
    @GET("api/1.0/")
    Call<QuoteResponse> getQuote(
            @Query("method") String method,
            @Query("format") String format,
            @Query("lang") String lang
    );
}