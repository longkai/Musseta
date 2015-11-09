package yuejia.liu.musseta;

import java.io.File;

import javax.inject.Singleton;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Application scope modules.
 */
public class MussetaModules {
  @Module
  static class ApplicationModule {
    final Application application;

    ApplicationModule(Application application) {
      this.application = application;
    }

    @Provides @Singleton Application providesApplication() {
      return application;
    }
  }

  @Module
  static class NetworkModule {
    @Provides @Singleton OkHttpClient providesOkHttpClient(Application application) {
      OkHttpClient client = new OkHttpClient();
      client.setCache(new Cache(new File(application.getCacheDir().getPath() + File.separator + "okHttp"), 50L * 1024 * 1024));
      return client;
    }

    @Provides @Singleton Picasso providesPicasso(Application application, OkHttpClient okHttpClient) {
      return new Picasso.Builder(application)
          .downloader(new OkHttpDownloader(okHttpClient))
          .listener((picasso, uri, exception) -> Timber.e(exception, "download [%s] fail", uri))
          .loggingEnabled(BuildConfig.DEBUG)
          .indicatorsEnabled(BuildConfig.DEBUG)
          .build();
    }
  }

  @Module
  static class StorageModule {
    @Provides @Singleton SharedPreferences providesPreferences(Application application) {
      return PreferenceManager.getDefaultSharedPreferences(application);
    }
  }

  @Module
  static class UtilityModule {
    @Provides @Singleton Gson providesGson() {
      GsonBuilder builder = new GsonBuilder();
      if (BuildConfig.DEBUG) {
        builder.setPrettyPrinting();
      }
      return builder.create();
    }
  }
}
