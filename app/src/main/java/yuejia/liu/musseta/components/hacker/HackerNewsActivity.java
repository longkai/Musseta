package yuejia.liu.musseta.components.hacker;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import timber.log.Timber;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.ui.MussetaActivity;

/**
 * Bootstrap ui.
 *
 * @author longkai
 */
public class HackerNewsActivity extends MussetaActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.musseta_default);

    setupToolbar();

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragment_container, new HackerNewsGridFragment())
          .commit();
    }

    Timber.d("bootstrapped...");
  }

  public void setToolbarColor(int color) {
    if (getSupportActionBar() == null) {
      View toolbar = findViewById(R.id.toolbar);
      if (toolbar != null) {
        toolbar.setBackgroundColor(color);
      }
    } else {
      getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }
  }

  @Override protected void setupActivityComponent() {}

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }
}
