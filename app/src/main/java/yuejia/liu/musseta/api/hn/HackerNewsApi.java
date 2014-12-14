package yuejia.liu.musseta.api.hn;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import yuejia.liu.musseta.model.hn.Changes;
import yuejia.liu.musseta.model.hn.Item;
import yuejia.liu.musseta.model.hn.User;

/**
 * The Hacker News api.
 *
 * @author longkai
 */
public interface HackerNewsApi {
  @GET("/topstories.json") long[] topStories();

  @GET("/item/{id}.json") Item item(@Path("id") long id);

  @GET("/user/{id}.json") Observable<User> user(@Path("id") String id);

  @GET("/maxitem.json") Observable<Long> maxItem();

  @GET("/updates.json") Observable<Changes> updates();
}
