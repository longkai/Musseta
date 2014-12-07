package yuejia.liu.musseta;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import dagger.ObjectGraph;
import yuejia.liu.musseta.arch.ActivityModule;

/**
 * Musseta base activity.
 *
 * @author longkai
 */
public class MussetaActivity extends ActionBarActivity {
  private ObjectGraph activityGraph;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityGraph = ((Musseta) getApplication()).getApplicationGraph().plus(new ActivityModule(this));
    activityGraph.inject(this);
  }

  public void inject(Object object) {
    activityGraph.inject(object);
  }

  @Override protected void onDestroy() {
    activityGraph = null;
    super.onDestroy();
  }
}
