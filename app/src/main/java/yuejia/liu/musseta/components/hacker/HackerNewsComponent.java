package yuejia.liu.musseta.components.hacker;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityScope;

/**
 * Created by longkai on 11/8/15.
 */
@ActivityScope
@Subcomponent(modules = HackerNewsModule.class)
public interface HackerNewsComponent {
  void inject(HackerNewsGridFragment fragment);
}
