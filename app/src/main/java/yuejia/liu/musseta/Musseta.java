package yuejia.liu.musseta;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Musseta application.
 */
public class Musseta extends Application {
  protected MussetaComponent applicationComponent;

  @Override public void onCreate() {
    super.onCreate();

    setupAppComponent();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
      LeakCanary.install(this);
    } else {
      Timber.plant(new Timber.Tree() {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
          if (priority == Log.WARN) {
            Crashlytics.log(priority, tag, message);
          } else if (priority >= Log.ERROR) {
            Crashlytics.logException(t);
          }
        }
      });
      Fabric.with(this, new Crashlytics());
    }
  }

  protected void setupAppComponent() {
    applicationComponent = DaggerMussetaComponent.builder()
        .applicationModule(new MussetaModules.ApplicationModule(this))
        .build();
  }

  public MussetaComponent getMussetaComponent() {
    return applicationComponent;
  }

  public static Musseta get(Context context) {
    return (Musseta) context.getApplicationContext();
  }
}
