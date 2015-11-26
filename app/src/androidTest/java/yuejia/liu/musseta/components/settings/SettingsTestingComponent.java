package yuejia.liu.musseta.components.settings;

import dagger.Subcomponent;
import yuejia.liu.musseta.components.ActivityScope;
import yuejia.liu.musseta.components.TestingActivityComponent;

/**
 * Created by longkai on 11/27/15.
 */
@ActivityScope
@Subcomponent
public interface SettingsTestingComponent extends SettingsComponent, TestingActivityComponent<SettingsActivityTest> {
}
