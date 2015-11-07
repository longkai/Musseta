package yuejia.liu.musseta;

import javax.inject.Singleton;

import dagger.Component;
import yuejia.liu.musseta.components.hacker.HackerNewsComponent;
import yuejia.liu.musseta.components.hacker.HackerNewsModule;

/**
 * Application components, allow sub-component to attach.
 */
@Singleton
@Component(modules = {
    MussetaModules.UtilityModule.class,
    MussetaModules.NetworkModule.class,
    MussetaModules.ApplicationModule.class
})
public interface MussetaComponent {
  HackerNewsComponent plus(HackerNewsModule module);
}
