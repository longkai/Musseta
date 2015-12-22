package yuejia.liu.musseta.components.home.product;

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
 * Product Hunt webview delegate.
 */
public class ProductWebViewDelegate implements WebViewDelegate {
  private final Post post;

  public ProductWebViewDelegate(Post post) {
    this.post = post;
  }

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {dest.writeParcelable(this.post, 0);}

  protected ProductWebViewDelegate(Parcel in) {this.post = in.readParcelable(Post.class.getClassLoader());}


  @Override public String title(WebActivity activity) {
    return activity.getString(R.string.product_hunt);
  }

  @Override public String subtitle(WebActivity activity) {
    return null;
  }

  @Override public String url(WebActivity activity) {
    return post.discussion_url;
  }

  @Override public boolean usePageTitle(WebActivity activity) {
    return false;
  }

  @Override public void onCreate(WebActivity activity, WebView webView) {
    int color = ContextCompat.getColor(activity, R.color.product_hunt_accent);
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
    return new Pair<>(post.name, activity.getString(R.string.product_hunt_share_content, post.tagline, post.discussion_url));
  }

  public static final Creator<ProductWebViewDelegate> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<ProductWebViewDelegate>() {
    @Override public ProductWebViewDelegate createFromParcel(Parcel in, ClassLoader loader) {
      return new ProductWebViewDelegate(in);
    }

    @Override public ProductWebViewDelegate[] newArray(int size) {
      return new ProductWebViewDelegate[size];
    }
  });
}
