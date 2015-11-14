package yuejia.liu.musseta;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnitRunner;

/**
 * Customized testing application context.
 */
public class MussetaTestingRunner extends AndroidJUnitRunner {
  @Override public Application newApplication(ClassLoader cl, String className, Context context)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return super.newApplication(cl, MussetaTesting.class.getName(), context);
  }

  @Override public void callActivityOnCreate(Activity activity, Bundle bundle) {
    if (hooks != null) {
      hooks.callActivityOnCreate(activity, bundle);
    }
    super.callActivityOnCreate(activity, bundle);
  }

  private Hooks hooks;

  public void addHooks(Hooks hooks) {
    this.hooks = hooks;
  }

  public void removeHooks() {
    this.hooks = null;
  }

  public interface Hooks {
    void callActivityOnCreate(Activity activity, Bundle bundle);
  }

  public static MussetaTestingRunner get() {
    return (MussetaTestingRunner) InstrumentationRegistry.getInstrumentation();
  }
}
