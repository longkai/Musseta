package yuejia.liu.musseta.components.home;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityComponent;
import yuejia.liu.musseta.components.ActivityScope;
import yuejia.liu.musseta.components.home.hacker.HackerNewsLayout;

/**
 * The Home component.
 */
@ActivityScope
@Subcomponent(modules = HomeModule.class)
public interface HomeComponent extends ActivityComponent<HomeActivity> {
  void inject(HackerNewsLayout layout);
}
