package yuejia.liu.musseta.components.hacker;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityScope;
import yuejia.liu.musseta.components.TestingActivityComponent;

/**
 * Created by longkai on 11/9/15.
 */
@ActivityScope
@Subcomponent(modules = HackerNewsModule.class)
public interface HackerNewsTestingComponent extends HackerNewsComponent, TestingActivityComponent<HackerNewsActivityTest> {
}
