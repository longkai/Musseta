package yuejia.liu.musseta.components.about;

import javax.inject.Inject;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.Space;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import yuejia.liu.musseta.BuildConfig;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.web.SimpleWebViewDelegate;
import yuejia.liu.musseta.components.web.WebActivity;
import yuejia.liu.musseta.ui.MussetaActivity;

/**
 * About UI.
 */
public class AboutActivity extends MussetaActivity<AboutComponent> {
  @Inject Picasso picasso;

  @Bind(R.id.place_holder)       Space     placeHolder;
  @Bind(R.id.app_bar)            ViewGroup appBarLayout;
  @Bind(R.id.toolbar)            Toolbar   toolbar;
  @Bind(R.id.app_name)           TextView  appName;
  @Bind(android.R.id.background) ImageView background;

  private final Object picassoTag = new Object();

  @Override protected AboutComponent setupActivityComponent() {
    return Musseta.get(this).getMussetaComponent().aboutComponent();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    ButterKnife.bind(this);

    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) placeHolder.getLayoutParams();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      lp.height = getStatusBarHeight();
      placeHolder.setLayoutParams(lp);
    }
    appBarLayout.setBackgroundResource(R.drawable.about_gradient);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    RequestCreator requestCreator = picasso.load(R.mipmap.background_about).tag(this).resize(displayMetrics.widthPixels, 0);
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      requestCreator.rotate(90);
    }
    requestCreator.into(background);

    String appNameString = getString(R.string.app_name);
    String text = String.format("%sv%s", appNameString, BuildConfig.VERSION_NAME);
    SpannableString string = SpannableString.valueOf(text);
    int start = appNameString.length();
    string.setSpan(new RelativeSizeSpan(.25f), start, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    appName.setText(string);
  }

  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }

  @Override protected void onResume() {
    super.onRestart();
    picasso.resumeTag(picassoTag);
  }

  @Override protected void onPause() {
    super.onPause();
    picasso.pauseTag(picassoTag);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    picasso.cancelTag(picassoTag);
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

  public void develop(View view) {
    WebActivity.startActivity(this, SimpleWebViewDelegate.newBuilder().usePageTitle(true).url("http://longist.me").build());
  }

  public void graphics(View view) {
    Toast.makeText(this, ":)", Toast.LENGTH_SHORT).show();
  }
}
