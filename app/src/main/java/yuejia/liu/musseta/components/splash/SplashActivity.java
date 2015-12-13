package yuejia.liu.musseta.components.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.components.home.HomeActivity;
import yuejia.liu.musseta.ui.MussetaActivity;

/**
 * Splash UI.
 */
public class SplashActivity extends MussetaActivity<SplashComponent> {
  @Override protected SplashComponent setupActivityComponent() {
    return Musseta.get(this).getMussetaComponent().splashComponent();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(new SplashView(this));

    handler.postDelayed(splashRunner, SplashComponent.splash_millis);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(splashRunner);
  }

  private Handler handler = new Handler(Looper.getMainLooper());

  private Runnable splashRunner = () -> startActivity(new Intent(SplashActivity.this, HomeActivity.class));
}
