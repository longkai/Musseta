package yuejia.liu.musseta;

import java.io.File;

import javax.inject.Singleton;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import timber.log.Timber;
import yuejia.liu.musseta.misc.NetworkWatcher;

/**
 * Application scope modules.
 */
public class MussetaModules {
  @Module
  public static class ApplicationModule {
    final Application application;

    public ApplicationModule(Application application) {
      this.application = application;
    }

    @Provides @Singleton protected Application providesApplication() {
      return application;
    }

    @Provides @Singleton protected GoogleAnalytics providesGoogleAnalytics(SharedPreferences sharedPreferences) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(application);
      analytics.setDryRun(BuildConfig.DEBUG); // when debug, disable tracking
      analytics.setAppOptOut(sharedPreferences.getBoolean(application.getString(R.string.pref_enable_tracking), false));
      return analytics;
    }

    @Provides @Singleton protected Tracker providesTracker(GoogleAnalytics analytics) {
      int identifier = application.getResources().getIdentifier("global_tracker", "xml", application.getPackageName());
      Tracker tracker = analytics.newTracker(identifier);
      tracker.enableAutoActivityTracking(true);
      return tracker;
    }
  }

  @Module
  public static class NetworkModule {
    @Provides @Singleton protected OkHttpClient providesOkHttpClient(Application application) {
      OkHttpClient client = new OkHttpClient();
      client.setCache(new Cache(new File(application.getCacheDir().getPath() + File.separator + "okHttp"), 50L * 1024 * 1024));
      return client;
    }

    @Provides @Singleton protected Picasso providesPicasso(Application application, OkHttpClient okHttpClient) {
      return new Picasso.Builder(application)
          .downloader(new OkHttpDownloader(okHttpClient))
          .listener((picasso, uri, exception) -> Timber.e(exception, "download [%s] fail", uri))
          .loggingEnabled(BuildConfig.DEBUG)
          .indicatorsEnabled(BuildConfig.DEBUG)
          .build();
    }

    @Provides @Singleton protected NetworkWatcher providesNetworkWatcher(Application application) {
      return new NetworkWatcher(application);
    }
  }

  @Module
  public static class StorageModule {
    @Provides @Singleton protected SharedPreferences providesPreferences(Application application) {
      return PreferenceManager.getDefaultSharedPreferences(application);
    }
  }

  @Module
  public static class UtilityModule {
    @Provides @Singleton protected Gson providesGson() {
      GsonBuilder builder = new GsonBuilder();
      if (BuildConfig.DEBUG) {
        builder.setPrettyPrinting();
      }
      return builder.create();
    }
  }
}
