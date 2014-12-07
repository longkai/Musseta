package yuejia.liu.musseta.arch;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import yuejia.liu.musseta.Musseta;

import javax.inject.Singleton;

/**
 * Android module.
 *
 * @author longkai
 */
@Module(includes = HttpModule.class, library = true)
public class AndroidModule {
  private final Musseta musseta;

  public AndroidModule(Musseta musseta) {
    this.musseta = musseta;
  }

  @Provides @Singleton Application provideApplication() {
    return musseta;
  }
}
