package yuejia.liu.musseta.api.hn;

import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import javax.inject.Singleton;

/**
 * Hacker News api module.
 *
 * @author longkai
 */
@Module(complete = false, library = true)
public class HackerNewsApiModule {
  public static final String END_POINT = "https://hacker-news.firebaseio.com/v0";

  @Provides @Singleton @HackerNews RestAdapter provideHNAdapter(OkHttpClient client) {
    return new RestAdapter.Builder()
        .setEndpoint(END_POINT)
        .setClient(new OkClient(client))
        .build();
  }

  @Provides @Singleton HackerNewsApi provideHackerNewsApi(@HackerNews RestAdapter adapter) {
    return adapter.create(HackerNewsApi.class);
  }
}
