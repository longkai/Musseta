package yuejia.liu.musseta.components.home;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import yuejia.liu.musseta.BuildConfig;
import yuejia.liu.musseta.components.ActivityScope;
import yuejia.liu.musseta.components.home.hacker.HackerNews;
import yuejia.liu.musseta.components.home.hacker.HackerNewsApi;

/**
 * The Home module.
 */
@Module
public class HomeModule {
  private final HomeActivity homeActivity;

  public HomeModule(HomeActivity homeActivity) {
    this.homeActivity = homeActivity;
  }

  @Provides @ActivityScope @HackerNews protected RestAdapter providesRestAdapter(OkHttpClient okHttpClient, Gson gson) {
    return new RestAdapter.Builder()
        .setClient(new OkClient(okHttpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .setEndpoint("https://hacker-news.firebaseio.com/v0")
        .build();
  }

  @Provides @ActivityScope protected HackerNewsApi providesHackerNewsApi(@HackerNews RestAdapter restAdapter) {
    return restAdapter.create(HackerNewsApi.class);
  }
}
