package yuejia.liu.musseta.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import timber.log.Timber;
import yuejia.liu.musseta.MussetaActivity;
import yuejia.liu.musseta.R;

/**
 * Bootstrap ui.
 *
 * @author longkai
 */
public class BootstrapActivity extends MussetaActivity {
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

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }
}
