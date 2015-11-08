package yuejia.liu.musseta.components.hacker;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityScope;

/**
 * The Hacker News module.
 */
@ActivityScope
@Subcomponent(modules = HackerNewsModule.class)
public interface HackerNewsComponent {
  void inject(HackerNewsActivity activity);
}
