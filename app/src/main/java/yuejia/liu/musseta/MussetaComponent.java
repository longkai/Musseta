package yuejia.liu.musseta;

import javax.inject.Singleton;

import dagger.Component;
import yuejia.liu.musseta.components.about.AboutComponent;
import yuejia.liu.musseta.components.home.HomeComponent;
import yuejia.liu.musseta.components.home.HomeModule;
import yuejia.liu.musseta.components.settings.SettingsComponent;
import yuejia.liu.musseta.components.splash.SplashComponent;
import yuejia.liu.musseta.components.web.WebComponent;

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
  SplashComponent splashComponent();

  SettingsComponent settingsComponent();

  AboutComponent aboutComponent();

  WebComponent webComponent();

  HomeComponent homeComponent(HomeModule module);
}
