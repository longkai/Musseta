package yuejia.liu.musseta.components.splash;

import android.os.Bundle;

import yuejia.liu.musseta.Musseta;
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
  }
}
