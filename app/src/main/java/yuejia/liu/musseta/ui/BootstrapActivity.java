package yuejia.liu.musseta.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
          .replace(R.id.fragment_container, new HackerNewsFragment())
          .commit();
    }

    Timber.d("bootstrapped...");
  }

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(getTitle());
    toolbar.inflateMenu(R.menu.main);
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem menuItem) {
        // TODO:
        return false;
      }
    });
  }
}
