package yuejia.liu.musseta.components.web;

import android.os.Parcel;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * Simple impl for webview activity delegate.
 */
public class SimpleWebViewDelegate implements WebViewDelegate {
  private String  url;
  private String  title;
  private String  subtitle;
  private boolean usePageTitle;

  private SimpleWebViewDelegate(Builder builder) {
    url = builder.url;
    title = builder.title;
    subtitle = builder.subtitle;
    usePageTitle = builder.usePageTitle;
  }

  public static Builder newBuilder() {return new Builder();}

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.url);
    dest.writeString(this.title);
    dest.writeString(this.subtitle);
    dest.writeByte(usePageTitle ? (byte) 1 : (byte) 0);
  }

  public SimpleWebViewDelegate() {}

  public SimpleWebViewDelegate(String url, String title, String subtitle, boolean usePageTitle) {
    this.url = url;
    this.title = title;
    this.subtitle = subtitle;
    this.usePageTitle = usePageTitle;
  }

  protected SimpleWebViewDelegate(Parcel in) {
    this.url = in.readString();
    this.title = in.readString();
    this.subtitle = in.readString();
    this.usePageTitle = in.readByte() != 0;
  }

  @Override public String title(WebActivity activity) {
    return title;
  }

  @Override public String subtitle(WebActivity activity) {
    return subtitle;
  }

  @Override public String url(WebActivity activity) {
    return url;
  }

  @Override public boolean usePageTitle(WebActivity activity) {
    return usePageTitle;
  }

  @Override public void onCreate(WebActivity activity, WebView webView) {
    // noop
  }

  @Override public void onCreateOptionsMenu(WebActivity activity, Menu menu) {
    // noop
  }

  @Override public boolean onOptionsItemSelected(WebActivity activity, MenuItem item) {
    return false;
  }

  @Override public Pair<String, String> onShare(WebActivity activity, WebView webView) {
    return new Pair<>(webView.getTitle(), url);
  }

  public static final Creator<SimpleWebViewDelegate> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SimpleWebViewDelegate>() {
    @Override public SimpleWebViewDelegate createFromParcel(Parcel in, ClassLoader loader) {
      return new SimpleWebViewDelegate(in);
    }

    @Override public SimpleWebViewDelegate[] newArray(int size) {
      return new SimpleWebViewDelegate[size];
    }
  });


  public static final class Builder {
    private String url;
    private String title;
    private String subtitle;
    private boolean usePageTitle;

    private Builder() {}

    public Builder url(String val) {
      url = val;
      return this;
    }

    public Builder title(String val) {
      title = val;
      return this;
    }

    public Builder subtitle(String val) {
      subtitle = val;
      return this;
    }

    public Builder usePageTitle(boolean val) {
      usePageTitle = val;
      return this;
    }

    public SimpleWebViewDelegate build() {return new SimpleWebViewDelegate(this);}
  }
}
