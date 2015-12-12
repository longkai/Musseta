package yuejia.liu.musseta.components.home;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityComponent;
import yuejia.liu.musseta.components.ActivityScope;
import yuejia.liu.musseta.components.home.dribbble.ShotsLayout;
import yuejia.liu.musseta.components.home.hacker.HackerNewsLayout;
import yuejia.liu.musseta.components.home.product.ProductHuntLayout;

/**
 * The Home component.
 */
@ActivityScope
@Subcomponent(modules = HomeModule.class)
public interface HomeComponent extends ActivityComponent<HomeActivity> {
  void inject(HackerNewsLayout layout);

  void inject(ProductHuntLayout layout);

  void inject(ShotsLayout layout);
}
