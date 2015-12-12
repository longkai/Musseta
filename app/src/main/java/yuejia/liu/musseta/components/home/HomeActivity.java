package yuejia.liu.musseta.components.home;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.analytics.Tracker;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.home.dribbble.ShotsLayout;
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
    tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.material_white));
    tabLayout.setupWithViewPager(viewPager);
  }

  public Tracker getTracker() {
    return tracker;
  }

  private static class HomePageAdapter extends StatePagerAdapter {
    private final String[] titles;

    public HomePageAdapter(Context context) {
      titles = context.getResources().getStringArray(R.array.home_pager_titles);
    }

    @Override public View instantiateView(ViewGroup container, int position) {
//      int layoutResId;
//      switch (position) {
//        case 0:
////          layoutResId = R.layout.layout_hacker_news;
//          layoutResId = R.layout.layout_product_hunt;
//          break;
//        case 1:
//          layoutResId = R.layout.layout_product_hunt;
//          break;
//        default:
//          throw new IllegalArgumentException("No such position " + position);
//      }
//      return LayoutInflater.from(container.getContext()).inflate(layoutResId, container, false);
      ShotsLayout layout = new ShotsLayout(container.getContext());
      layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
