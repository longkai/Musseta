package yuejia.liu.musseta.components.home.hacker;

import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.web.SimpleWebViewDelegate;
import yuejia.liu.musseta.components.web.WebActivity;
import yuejia.liu.musseta.components.web.WebViewDelegate;

/**
 * Created by longkai on 12/15/15.
 */
public class HackerNewsWebDelegate implements WebViewDelegate {

  private Item item;

  public HackerNewsWebDelegate(Item item) {
    this.item = item;
  }

  @Override public String title(WebActivity activity) {
    return activity.getString(R.string.hacker_news);
  }

  @Override public String subtitle(WebActivity activity) {
    return null;
  }

  @Override public String url(WebActivity activity) {
    return item.url;
  }

  @Override public boolean usePageTitle(WebActivity activity) {
    return false;
  }

  @Override public void onCreate(WebActivity activity, WebView webView) {
    int accent = ContextCompat.getColor(activity, R.color.hacker_news_accent);
    activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(accent));
    activity.getUiToolkit().setStatusBarColor(accent);
    activity.getUiToolkit().setTaskDescription(null, null, accent);
  }

  @Override public void onCreateOptionsMenu(WebActivity activity, Menu menu) {
    MenuItem comments = menu.add(Menu.NONE, android.R.id.button1, Menu.NONE, activity.getString(R.string.comments_count, item.descendants));
    comments.setIcon(new CommentsOutline(activity, item.descendants));
    MenuItemCompat.setShowAsAction(comments, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
  }

  @Override public boolean onOptionsItemSelected(WebActivity activity, MenuItem item) {
    if (item.getItemId() == android.R.id.button1) {
      WebActivity.startActivity(activity, SimpleWebViewDelegate.newBuilder()
          .url(activity.getString(R.string.hacker_news_comments_url, this.item.id))
          .subtitle(this.item.title)
          .title(activity.getString(R.string.comments)).build());
      return true;
    }
    return false;
  }

  @Override public Pair<String, String> onShare(WebActivity activity, WebView webView) {
    return new Pair<>(item.title, activity.getString(R.string.hacker_news_share_content, item.title, item.url));
  }

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {dest.writeParcelable(this.item, 0);}

  protected HackerNewsWebDelegate(Parcel in) {this.item = in.readParcelable(Item.class.getClassLoader());}

  public static final Creator<HackerNewsWebDelegate> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<HackerNewsWebDelegate>() {
    @Override public HackerNewsWebDelegate createFromParcel(Parcel in, ClassLoader loader) {
      return new HackerNewsWebDelegate(in);
    }

    @Override public HackerNewsWebDelegate[] newArray(int size) {
      return new HackerNewsWebDelegate[size];
    }
  });
}
