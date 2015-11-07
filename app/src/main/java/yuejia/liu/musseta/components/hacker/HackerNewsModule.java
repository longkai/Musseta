package yuejia.liu.musseta.components.hacker;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import yuejia.liu.musseta.BuildConfig;
import yuejia.liu.musseta.components.ActivityScope;

/**
 * The Hacker News module.
 */
@Module
public class HackerNewsModule {
  @Provides @ActivityScope @HackerNews RestAdapter providesRestAdapter(OkHttpClient okHttpClient, Gson gson) {
    return new RestAdapter.Builder()
        .setClient(new OkClient(okHttpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .setEndpoint("https://hacker-news.firebaseio.com/v0")
        .build();
  }

  @Provides @ActivityScope HackerNewsApi providesHackerNewsApi(@HackerNews RestAdapter restAdapter) {
    return restAdapter.create(HackerNewsApi.class);
  }
}
