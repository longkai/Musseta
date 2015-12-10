package yuejia.liu.musseta.components.home;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import yuejia.liu.musseta.BuildConfig;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.ActivityScope;
import yuejia.liu.musseta.components.home.hacker.HackerNews;
import yuejia.liu.musseta.components.home.hacker.HackerNewsApi;
import yuejia.liu.musseta.components.home.product.ProductHunt;
import yuejia.liu.musseta.components.home.product.ProductHuntApi;

/**
 * The Home module.
 */
@Module
public class HomeModule {
  private final HomeActivity homeActivity;

  public HomeModule(HomeActivity homeActivity) {
    this.homeActivity = homeActivity;
  }

  @Provides @ActivityScope @HackerNews
  protected RestAdapter providesHackerNewsRestAdapter(OkHttpClient okHttpClient, Gson gson) {
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

  @Provides @ActivityScope @ProductHunt
  protected RestAdapter providesProductHuntRestAdapter(OkHttpClient okHttpClient, Gson gson, SharedPreferences pref) {
    return new RestAdapter.Builder()
        .setClient(new OkClient(okHttpClient))
        .setConverter(new GsonConverter(gson))
        .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
        .setEndpoint("https://api.producthunt.com/v1")
        .setRequestInterceptor(request -> {
          String token = pref.getString(homeActivity.getString(R.string.product_hunt_access_token), null);
          if (token != null) {
            request.addHeader("Authorization", "Bearer " + token);
          }
          request.addHeader("Host", "api.producthunt.com");
          request.addHeader("Accept", "application/json");
          request.addHeader("Content-Type", "application/json");
        })
        .build();
  }

  @Provides @ActivityScope protected ProductHuntApi providesProductHuntApi(@ProductHunt RestAdapter restAdapter) {
    return restAdapter.create(ProductHuntApi.class);
  }
}
