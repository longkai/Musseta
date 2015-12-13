package yuejia.liu.musseta.components.home;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.analytics.Tracker;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.home.dribbble.ShotsLayout;
import yuejia.liu.musseta.components.home.hacker.HackerNewsLayout;
import yuejia.liu.musseta.components.home.product.ProductHuntLayout;
import yuejia.liu.musseta.components.settings.SettingsActivity;
import yuejia.liu.musseta.ui.MussetaActivity;
import yuejia.liu.musseta.ui.StatePagerAdapter;

/**
 * Home UI.
 */
public class HomeActivity extends MussetaActivity<HomeComponent> {
  @Bind(R.id.root_view)    CoordinatorLayout coordinatorLayout;
  @Bind(R.id.app_bar)      AppBarLayout      appBarLayout;
  @Bind(R.id.toolbar)      Toolbar           toolbar;
  @Bind(android.R.id.tabs) TabLayout         tabLayout;
  @Bind(R.id.view_pager)   ViewPager         viewPager;

  @Inject Tracker tracker;

  @Override protected HomeComponent setupActivityComponent() {
    return Musseta.get(this).getMussetaComponent().plus(new HomeModule(this));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_home);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);

    viewPager.setAdapter(new HomePageAdapter(this));
    tabLayout.setupWithViewPager(viewPager);
  }

  public Tracker getTracker() {
    return tracker;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private static class HomePageAdapter extends StatePagerAdapter {
    private final String[] titles;

    public HomePageAdapter(Context context) {
      titles = context.getResources().getStringArray(R.array.home_pager_titles);
    }

    @Override public View instantiateView(ViewGroup container, int position) {
      final Context context = container.getContext();
      View layout;
      switch (position) {
        case 0:
          layout = new HackerNewsLayout(context);
          break;
        case 1:
          layout = new ProductHuntLayout(context);
          break;
        case 2:
          layout = new ShotsLayout(context);
          break;
        default:
          throw new IllegalArgumentException("No such position " + position);
      }
      return layout;
    }

    @Override public int getCount() {
      return titles.length;
    }

    @Override public CharSequence getPageTitle(int position) {
      return titles[position];
    }
  }
}
