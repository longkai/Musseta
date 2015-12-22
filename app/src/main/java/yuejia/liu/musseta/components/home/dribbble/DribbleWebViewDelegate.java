package yuejia.liu.musseta.components.home.dribbble;

import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.web.WebActivity;
import yuejia.liu.musseta.components.web.WebViewDelegate;

/**
 * Simple dribbble web view delegate.
 */
public class DribbleWebViewDelegate implements WebViewDelegate {
  private final Shot shot;

  public DribbleWebViewDelegate(Shot shot) {
    this.shot = shot;
  }

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {dest.writeParcelable(this.shot, 0);}

  protected DribbleWebViewDelegate(Parcel in) {this.shot = in.readParcelable(Shot.class.getClassLoader());}

  @Override public String title(WebActivity activity) {
    return activity.getString(R.string.dribbble);
  }

  @Override public String subtitle(WebActivity activity) {
    return null;
  }

  @Override public String url(WebActivity activity) {
    return shot.html_url;
  }

  @Override public boolean usePageTitle(WebActivity activity) {
    return false;
  }

  @Override public void onCreate(WebActivity activity, WebView webView) {
    int color = ContextCompat.getColor(activity, R.color.dribbble_accent);
    activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    activity.getUiToolkit().setStatusBarColor(color);
    activity.getUiToolkit().setTaskDescription(null, null, color);
  }

  @Override public void onCreateOptionsMenu(WebActivity activity, Menu menu) {

  }

  @Override public boolean onOptionsItemSelected(WebActivity activity, MenuItem item) {
    return false;
  }

  @Override public Pair<String, String> onShare(WebActivity activity, WebView webView) {
    return new Pair<>(shot.title, activity.getString(R.string.dribbble_share_content, shot.html_url, shot.description));
  }

  public static final Creator<DribbleWebViewDelegate> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<DribbleWebViewDelegate>() {
    @Override public DribbleWebViewDelegate createFromParcel(Parcel in, ClassLoader loader) {
      return new DribbleWebViewDelegate(in);
    }

    @Override public DribbleWebViewDelegate[] newArray(int size) {
      return new DribbleWebViewDelegate[size];
    }
  });
}
