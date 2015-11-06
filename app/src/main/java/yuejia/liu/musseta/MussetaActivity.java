package yuejia.liu.musseta;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Musseta base activity.
 *
 * @author longkai
 */
public class MussetaActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
