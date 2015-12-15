package yuejia.liu.musseta.components.web;

import android.os.Parcelable;
import android.support.v4.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * A simple web view delegate.
 */
public interface WebViewDelegate extends Parcelable {
  String title(WebActivity activity);

  String subtitle(WebActivity activity);

  String url(WebActivity activity);

  boolean usePageTitle(WebActivity activity);

  void onCreate(WebActivity activity, WebView webView);

  void onCreateOptionsMenu(WebActivity activity, Menu menu);

  boolean onOptionsItemSelected(WebActivity activity, MenuItem item);

  Pair<String, String> onShare(WebActivity activity, WebView webView);
}
