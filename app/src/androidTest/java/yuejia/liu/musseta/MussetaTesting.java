package yuejia.liu.musseta;

import android.content.Context;

/**
 * Customized Musseta testing app.
 */
public class MussetaTesting extends Musseta {
  @Override protected void setupAppComponent() {
    applicationComponent = DaggerMussetaTestingComponent.builder()
        .applicationModule(new MussetaModules.ApplicationModule(this) {
          // TODO: 11/9/15 should we mock all the denpendencis here ?
        })
        .build();
  }

  public static MussetaTesting get(Context context) {
    return (MussetaTesting) context.getApplicationContext();
  }

  public MussetaTestingComponent getMussetaTestingComponent() {
    return (MussetaTestingComponent) applicationComponent;
  }
}
