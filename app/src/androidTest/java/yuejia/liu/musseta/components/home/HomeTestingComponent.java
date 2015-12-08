package yuejia.liu.musseta.components.home;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityScope;
import yuejia.liu.musseta.components.TestingActivityComponent;

/**
 * Home activity testing component.
 */
@ActivityScope
@Subcomponent(modules = HomeModule.class)
public interface HomeTestingComponent extends HomeComponent, TestingActivityComponent<HackerNewsLayoutTest> {
}
