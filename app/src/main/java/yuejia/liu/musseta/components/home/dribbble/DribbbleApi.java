package yuejia.liu.musseta.components.home.dribbble;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * The dirbbble api.
 */
public interface DribbbleApi {
  // omit some other options for now
  @GET("/shots") Observable<List<Shot>> shots(@Query("page") int page);
}
