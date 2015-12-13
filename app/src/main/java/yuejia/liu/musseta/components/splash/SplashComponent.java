package yuejia.liu.musseta.components.splash;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityComponent;
import yuejia.liu.musseta.components.ActivityScope;

/**
 * Say hello the our users:)
 */
@ActivityScope
@Subcomponent
public interface SplashComponent extends ActivityComponent<SplashActivity> {
  int splash_millis = 2000;
  int animation_millis = 1500;
  int slogan_millis = 300;
}
