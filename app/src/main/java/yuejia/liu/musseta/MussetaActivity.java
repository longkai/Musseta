package yuejia.liu.musseta;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
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

  public void setToolbarColor(int color) {
    if (getSupportActionBar() == null) {
      View toolbar = findViewById(R.id.toolbar);
      if (toolbar != null) {
        toolbar.setBackgroundColor(color);
      }
    } else {
      getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }
  }

  public void inject(Object object) {
    activityGraph.inject(object);
  }

  @Override protected void onDestroy() {
    activityGraph = null;
    super.onDestroy();
  }
}
