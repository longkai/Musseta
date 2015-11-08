package yuejia.liu.musseta;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Musseta application.
 */
public class Musseta extends Application {
  private MussetaComponent applicationComponent;

  @Override public void onCreate() {
    super.onCreate();

    setupAppComponent();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      // TODO: 11/9/15 separate test and production code
//      Timber.plant(new Timber.Tree() {
//        @Override protected void log(int priority, String tag, String message, Throwable t) {
//          if (priority == Log.WARN) {
//            Crashlytics.log(priority, tag, message);
//          } else if (priority >= Log.ERROR) {
//            Crashlytics.logException(t);
//          }
//        }
//      });
//      Fabric.with(this, new Crashlytics());
    }
  }

  private void setupAppComponent() {
    applicationComponent = DaggerMussetaComponent.builder()
        .applicationModule(new MussetaModules.ApplicationModule(this))
        .build();
  }

  public MussetaComponent getMussetaComponent() {
    return applicationComponent;
  }

  @VisibleForTesting public void setMussetaComponent(MussetaComponent applicationComponent) {
    this.applicationComponent = applicationComponent;
  }

  public static Musseta get(Context context) {
    return (Musseta) context.getApplicationContext();
  }
}
