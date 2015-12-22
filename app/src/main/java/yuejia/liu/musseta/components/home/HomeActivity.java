package yuejia.liu.musseta.components.home;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import com.google.android.gms.analytics.Tracker;
import timber.log.Timber;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.about.AboutActivity;
import yuejia.liu.musseta.components.home.dribbble.ShotsLayout;
import yuejia.liu.musseta.components.home.hacker.HackerNewsLayout;
import yuejia.liu.musseta.components.home.product.ProductHuntLayout;
import yuejia.liu.musseta.components.settings.SettingsActivity;
import yuejia.liu.musseta.ui.MussetaActivity;
import yuejia.liu.musseta.ui.UIToolkit;
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

  @BindColor(R.color.hacker_news_accent)  int hackerNewsAccent;
  @BindColor(R.color.product_hunt_accent) int productHuntAccent;
  @BindColor(R.color.dribbble_accent)     int dribbbleAccent;

  @Inject Tracker   tracker;
  @Inject UIToolkit uiToolkit;

  @Override protected HomeComponent setupActivityComponent() {
    return Musseta.get(this).getMussetaComponent().homeComponent(new HomeModule(this));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);

    viewPager.setPageTransformer(true, new HohoPageTransformer());
    viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.medium));

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
      case R.id.action_about:
        startActivity(new Intent(this, AboutActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private class HohoPageTransformer implements ViewPager.PageTransformer {
    final int r1 = Color.red(hackerNewsAccent);
    final int g1 = Color.green(hackerNewsAccent);
    final int b1 = Color.blue(hackerNewsAccent);

    final int r2 = Color.red(productHuntAccent);
    final int g2 = Color.green(productHuntAccent);
    final int b2 = Color.blue(productHuntAccent);

    final int r3 = Color.red(dribbbleAccent);
    final int g3 = Color.green(dribbbleAccent);
    final int b3 = Color.blue(dribbbleAccent);

    public HohoPageTransformer() {
      // init the first page if any...
      appBarLayout.setBackgroundColor(hackerNewsAccent);
      uiToolkit.setStatusBarColor(hackerNewsAccent);
    }

    @Override public void transformPage(View page, float position) {
      // TODO: 12/13/15 if page size larger than 3, how should we do?
      if (page instanceof ProductHuntLayout) {
        Timber.d("position %f", position);
        float ratio1 = Math.abs(position);
        float ratio2 = 1 - ratio1;
        int rgb = -1;
        if (position < 0 && position >= -1) { // 1 - 2
          rgb = Color.rgb((int) (r3 * ratio1 + r2 * ratio2), (int) (g3 * ratio1 + g2 * ratio2), (int) (b3 * ratio1 + b2 * ratio2));
        } else if (position > 0 && position <= 1) {  // 0 - 1
          rgb = Color.rgb((int) (r1 * ratio1 + r2 * ratio2), (int) (g1 * ratio1 + g2 * ratio2), (int) (b1 * ratio1 + b2 * ratio2));
        }
        if (rgb != -1) {
          appBarLayout.setBackgroundColor(rgb);
          uiToolkit.setStatusBarColor(rgb);
        }
      }
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
