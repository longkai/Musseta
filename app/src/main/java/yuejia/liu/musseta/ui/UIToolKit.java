package yuejia.liu.musseta.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnyRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

/**
 * The Resource manager for {@link yuejia.liu.musseta.components.ActivityScope}
 */
@UiThread
public class UIToolkit {
  final Activity activity;

  final TypedValue typedValue;

  public UIToolkit(Activity activity) {
    this.activity = activity;
    typedValue = new TypedValue();
  }

  @AnyRes public int getResourceId(@AttrRes int attr) {
    activity.getTheme().resolveAttribute(attr, typedValue, true);
    return typedValue.resourceId;
  }

  public void setStatusBarColor(int color) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.getWindow().setStatusBarColor(color);
    }
  }

  public void setTaskDescription(@Nullable String label, @Nullable Bitmap icon, int color) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.setTaskDescription(new ActivityManager.TaskDescription(label, icon, color));
    }
  }

  public void setSupportActionbar(Toolbar toolbar, @ColorRes int backIconColor) {
    if (activity instanceof AppCompatActivity) {
      AppCompatActivity a = (AppCompatActivity) activity;
      a.setSupportActionBar(toolbar);
      a.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      Drawable icon = toolbar.getNavigationIcon();
      icon = DrawableCompat.wrap(icon);
      DrawableCompat.setTint(icon, ContextCompat.getColor(a, backIconColor));
    }
  }

  public static void setBackground(View view, Drawable background) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      view.setBackground(background);
    } else {
      view.setBackgroundDrawable(background);
    }
  }
}
