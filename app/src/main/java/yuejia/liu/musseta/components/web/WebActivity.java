package yuejia.liu.musseta.components.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.ui.MussetaActivity;
import yuejia.liu.musseta.ui.UIToolkit;

/**
 * The global nested web activity.
 */
public class WebActivity extends MussetaActivity<WebComponent> {
  private static final int DOUBLE_SOUND_PLAY_DELAY_MILLIS = 100;

  @Bind(R.id.root_view)        LinearLayoutCompat linearLayout;
  @Bind(R.id.app_bar)          AppBarLayout       appBarLayout;
  @Bind(R.id.toolbar)          Toolbar            toolbar;
  @Bind(android.R.id.progress) ProgressBar        progressBar;
  @Bind(R.id.web_view)         WebView            webView;

  private UIToolkit       uiToolkit;
  private WebViewDelegate delegate;

  private Runnable delaySoundPlayer = () -> toolbar.playSoundEffect(SoundEffectConstants.CLICK);

  public static void startActivity(Context context, WebViewDelegate delegate) {
    Intent intent = new Intent(context, WebActivity.class);
    intent.putExtra(WebComponent.param_web_delegate, delegate);
    context.startActivity(intent);
  }

  @Override protected WebComponent setupActivityComponent() {
    return Musseta.get(this).getMussetaComponent().webComponent();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!getIntent().hasExtra(WebComponent.param_web_delegate)) {
      throw new IllegalArgumentException("Where is your web delegate?");
    }
    delegate = getIntent().getParcelableExtra(WebComponent.param_web_delegate);
    uiToolkit = new UIToolkit(this);

    setContentView(R.layout.activity_web);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(delegate.title(this));
    doubleTapBack2Top();
    if (!TextUtils.isEmpty(delegate.subtitle(this))) {
      getSupportActionBar().setSubtitle(delegate.subtitle(this));
    }

    delegate.onCreate(this, webView);

    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        webView.loadUrl(url);
        return true;
      }
    });
    webView.setWebChromeClient(new WebChromeClient() {
      @Override public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (delegate.usePageTitle(WebActivity.this)) {
          getSupportActionBar().setTitle(title);
        }
      }

      @Override public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        progressBar.setProgress(newProgress);
        if (newProgress == 100) {
          progressBar.setVisibility(View.GONE);
        } else {
          progressBar.setVisibility(View.VISIBLE);
        }
      }
    });

    webView.loadUrl(delegate.url(this));
  }

  private void doubleTapBack2Top() {
    GestureDetectorCompat compat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
      @Override public boolean onDown(MotionEvent e) {
        return true;
      }

      @Override public boolean onDoubleTap(MotionEvent e) {
        toolbar.postDelayed(delaySoundPlayer, DOUBLE_SOUND_PLAY_DELAY_MILLIS);
        webView.scrollTo(0, 0);
        return true;
      }

      @Override public boolean onSingleTapUp(MotionEvent e) {
        toolbar.playSoundEffect(SoundEffectConstants.CLICK);
        return super.onSingleTapUp(e);
      }
    });
    toolbar.setOnTouchListener((v, event) -> compat.onTouchEvent(event));
  }

  @Override public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      super.onBackPressed();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    delegate.onCreateOptionsMenu(this, menu);
    getMenuInflater().inflate(R.menu.web, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (delegate.onOptionsItemSelected(this, item)) {
      return true;
    }

    switch (item.getItemId()) {
      case R.id.refresh:
        webView.reload();
        return true;
      case R.id.open_browser:
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(webView.getUrl())));
        return true;
      case R.id.share:
        Pair<String, String> share = delegate.onShare(this, webView);
        startActivity(new Intent(Intent.ACTION_SEND)
            .setType(getString(R.string.mime_text_plain))
            .putExtra(Intent.EXTRA_TITLE, share.first)
            .putExtra(Intent.EXTRA_TEXT, share.second)
        );
        return true;
      case android.R.id.home:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    toolbar.removeCallbacks(delaySoundPlayer);
  }

  public UIToolkit getUiToolkit() {
    return uiToolkit;
  }
}
