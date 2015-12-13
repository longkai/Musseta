package yuejia.liu.musseta.components.settings;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.analytics.Tracker;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.ui.MussetaActivity;

/**
 * Settings UI.
 */
public class SettingsActivity extends MussetaActivity<SettingsComponent> {
  @Inject Tracker tracker;

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected SettingsComponent setupActivityComponent() {
    return Musseta.get(this).getMussetaComponent().settingsComponent();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_settings);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
      default:
      return super.onOptionsItemSelected(item);
    }
  }

  public static class SettingsFragment extends PreferenceFragmentCompat {
    @Override public void onCreatePreferences(Bundle bundle, String s) {
      addPreferencesFromResource(R.xml.settings);
    }
  }
}
