package yuejia.liu.musseta.components.hacker;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.subscriptions.CompositeSubscription;
import yuejia.liu.musseta.BuildConfig;
import yuejia.liu.musseta.components.ActivityScope;
import yuejia.liu.musseta.misc.NetworkWatcher;
import yuejia.liu.musseta.ui.ResourceManager;

/**
 * The Hacker News module.
 */
@Module
public class HackerNewsModule {
  private final HackerNewsActivity activity;

  public HackerNewsModule(HackerNewsActivity activity) {
    this.activity = activity;
  }

  @Provides @ActivityScope @HackerNews public RestAdapter providesRestAdapter(OkHttpClient okHttpClient, Gson gson) {
    return new RestAdapter.Builder()
        .setClient(new OkClient(okHttpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .setEndpoint("https://hacker-news.firebaseio.com/v0")
        .build();
  }

  @Provides @ActivityScope public HackerNewsApi providesHackerNewsApi(@HackerNews RestAdapter restAdapter) {
    return restAdapter.create(HackerNewsApi.class);
  }

  @Provides @ActivityScope @HackerNews public CompositeSubscription providesSubscriptions() {
    return new CompositeSubscription();
  }

  @Provides @ActivityScope
  public HackerNewsPresenter providesHackerNewsPresenter(NetworkWatcher networkWatcher, HackerNewsApi api, @HackerNews CompositeSubscription subscriptions) {
    return new HackerNewsPresenter(activity, api, subscriptions, networkWatcher);
  }

  @Provides @ActivityScope public ResourceManager providesResourceManager() {
    return new ResourceManager(activity);
  }
}
