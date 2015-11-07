package yuejia.liu.musseta.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Musseta base activity.
 */
public abstract class MussetaActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setupActivityComponent();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    // TODO: 11/8/15 Maybe should add a callback here to let sub activity component to release non-singleton?
  }

  protected abstract void setupActivityComponent();
}
