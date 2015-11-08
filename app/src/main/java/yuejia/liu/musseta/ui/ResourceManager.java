package yuejia.liu.musseta.ui;

import android.app.Activity;
import android.support.annotation.AnyRes;
import android.support.annotation.AttrRes;
import android.support.annotation.UiThread;
import android.util.TypedValue;

/**
 * The Resource manager for {@link yuejia.liu.musseta.components.ActivityScope}
 */
@UiThread
public class ResourceManager {
  final Activity activity;

  final TypedValue typedValue;

  public ResourceManager(Activity activity) {
    this.activity = activity;
    typedValue = new TypedValue();
  }

  @AnyRes public int getResourceId(@AttrRes int attr) {
    activity.getTheme().resolveAttribute(attr, typedValue, true);
    return typedValue.resourceId;
  }
}
