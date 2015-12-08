package yuejia.liu.musseta;

import javax.inject.Singleton;

import dagger.Component;
import yuejia.liu.musseta.components.home.HomeComponent;
import yuejia.liu.musseta.components.home.HomeModule;
import yuejia.liu.musseta.components.settings.SettingsComponent;

/**
 * Application components, allow sub-component to attach.
 */
@Singleton
@Component(modules = {
    MussetaModules.UtilityModule.class,
    MussetaModules.StorageModule.class,
    MussetaModules.NetworkModule.class,
    MussetaModules.ApplicationModule.class
})
public interface MussetaComponent {
  SettingsComponent plus();

  HomeComponent plus(HomeModule module);
}
