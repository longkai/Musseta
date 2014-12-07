package yuejia.liu.musseta.arch;

import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import javax.inject.Singleton;

/**
 * Api module.
 *
 * @author longkai
 */
@Module(complete = false, library = true)
public class ApiModule {
  public static final String END_POINT = "https://hacker-news.firebaseio.com/v0";

  @Provides @Singleton RestAdapter provideRestAdapter(OkHttpClient client) {
    return new RestAdapter.Builder()
        .setEndpoint(END_POINT)
        .setClient(new OkClient(client))
        .build();
  }
}
