package yuejia.liu.musseta;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import yuejia.liu.musseta.helper.CrashlyticsTree;

/**
 * Musseta app.
 *
 * @author longkai
 */
public class Musseta extends Application {
  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      Timber.plant(new CrashlyticsTree());
      Fabric.with(this, new Crashlytics());
    }
  }
}
