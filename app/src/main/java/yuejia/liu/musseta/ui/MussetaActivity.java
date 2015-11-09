package yuejia.liu.musseta.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import yuejia.liu.musseta.components.ActivityComponent;

/**
 * Musseta base activity.
 */
public abstract class MussetaActivity<T extends ActivityComponent> extends AppCompatActivity {
  protected T activityComponent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (activityComponent == null) {
      activityComponent = setupActivityComponent();
    }
    if (activityComponent != null) {
      activityComponent.inject(this);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    // TODO: 11/8/15 Maybe should add a callback here to let sub activity component to release non-singleton?
  }

  protected abstract T setupActivityComponent();

  public @NonNull T getActivityComponent() {
    return activityComponent;
  }

  /** This method should be called in testing, seriously */
  public void setActivityComponent(T activityComponent) {
    this.activityComponent = activityComponent;
  }
}
