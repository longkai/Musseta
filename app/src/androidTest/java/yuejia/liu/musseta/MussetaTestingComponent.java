package yuejia.liu.musseta;

import javax.inject.Singleton;

import dagger.Component;
import yuejia.liu.musseta.components.home.HomeModule;
import yuejia.liu.musseta.components.home.HomeTestingComponent;
import yuejia.liu.musseta.components.settings.SettingsTestingComponent;

/**
 * Copy from production code to add more sub-component.
 */
@Singleton
// TODO: 11/9/15 should we change to the testing specific ? currently we use method override
@Component(modules = {
    MussetaModules.UtilityModule.class,
    MussetaModules.StorageModule.class,
    MussetaModules.NetworkModule.class,
    MussetaModules.ApplicationModule.class
})
public interface MussetaTestingComponent extends MussetaComponent {
  SettingsTestingComponent settingsTestingComponent();

  HomeTestingComponent homeComponent(HomeModule module);
}
