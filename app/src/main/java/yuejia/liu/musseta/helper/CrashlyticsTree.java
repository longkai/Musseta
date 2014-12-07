package yuejia.liu.musseta.helper;

import com.crashlytics.android.Crashlytics;
import timber.log.Timber;

/**
 * Crashlytics debug helper.
 *
 * @author longkai
 */
public class CrashlyticsTree extends Timber.HollowTree {
  @Override public void i(String message, Object... args) {
    Crashlytics.log(String.format(message, args));
  }

  @Override public void e(Throwable t, String message, Object... args) {
    Crashlytics.logException(t);
  }
}
