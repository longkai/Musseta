package yuejia.liu.musseta.components.hacker;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * The Hacker News api.
 */
public interface HackerNewsApi {
  @GET("/topstories.json") long[] topStories();

  @GET("/item/{id}.json") Item item(@Path("id") long id);

  @GET("/maxitem.json") Observable<Long> maxItem();
}
