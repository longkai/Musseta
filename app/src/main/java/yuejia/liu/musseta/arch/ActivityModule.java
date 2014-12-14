package yuejia.liu.musseta.arch;

import dagger.Module;
import yuejia.liu.musseta.MussetaActivity;
import yuejia.liu.musseta.MussetaFragment;
import yuejia.liu.musseta.MussetaListFragment;
import yuejia.liu.musseta.MussetaRecyclerFragment;
import yuejia.liu.musseta.ui.BootstrapActivity;
import yuejia.liu.musseta.ui.HackerNewsGridFragment;

/**
 * Musseta activity module.
 *
 * @author longkai
 */
@Module(
    injects = {
        MussetaActivity.class,
        MussetaFragment.class,
        MussetaListFragment.class,
        MussetaRecyclerFragment.class,
        BootstrapActivity.class,
        HackerNewsGridFragment.class,
    },
    addsTo = AndroidModule.class,
    library = true
)
public class ActivityModule {
  private final MussetaActivity activity;

  public ActivityModule(MussetaActivity activity) {
    this.activity = activity;
  }
}
