package yuejia.liu.musseta.api.hn;

import javax.inject.Singleton;

import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Hacker News api module.
 *
 * @author longkai
 */
public class HackerNewsApiModule {
  public static final String END_POINT = "https://hacker-news.firebaseio.com/v0";

  @Singleton RestAdapter provideHNAdapter() {
    return new RestAdapter.Builder()
        .setEndpoint(END_POINT)
        .setClient(new OkClient(new OkHttpClient()))
        .build();
  }

  @Singleton public HackerNewsApi provideHackerNewsApi() {
    return provideHNAdapter().create(HackerNewsApi.class);
  }
}
