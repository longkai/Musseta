package yuejia.liu.musseta;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import yuejia.liu.musseta.arch.AndroidModule;

/**
 * Musseta app.
 *
 * @author longkai
 */
public class Musseta extends Application {
  private ObjectGraph applicationGraph;

  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
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

    applicationGraph = ObjectGraph.create(new AndroidModule(this));
  }

  public ObjectGraph getApplicationGraph() {
    return applicationGraph;
  }
}
