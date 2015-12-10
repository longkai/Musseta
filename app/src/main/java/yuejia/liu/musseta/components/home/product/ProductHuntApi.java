package yuejia.liu.musseta.components.home.product;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * The Product Hunt API.
 */
public interface ProductHuntApi {
  @POST("/oauth/token") Observable<Token> token(@Body Token.RequestTokenBody body);

  @GET("/posts") Observable<Response> posts(@Query("days_ago") int days_ago);
}
