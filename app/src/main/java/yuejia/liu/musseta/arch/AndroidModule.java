package yuejia.liu.musseta.arch;

import dagger.Module;
import yuejia.liu.musseta.Musseta;

/**
 * Android module.
 *
 * @author longkai
 */
@Module(library = true)
public class AndroidModule {
  private final Musseta musseta;

  public AndroidModule(Musseta musseta) {
    this.musseta = musseta;
  }
}
