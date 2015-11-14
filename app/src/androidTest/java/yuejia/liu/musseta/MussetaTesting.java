package yuejia.liu.musseta;

import android.content.Context;

/**
 * Customized Musseta testing app.
 */
public class MussetaTesting extends Musseta {
  // if the testing component change the default app component, after testing, use this to restore
  private MussetaComponent applicationComponentCopy;

  @Override protected void setupAppComponent() {
    applicationComponent = DaggerMussetaTestingComponent.builder()
        .applicationModule(new MussetaModules.ApplicationModule(this) {
          // TODO: 11/9/15 should we mock all the denpendencis here ?
        })
        .build();

    applicationComponentCopy = applicationComponent;
  }

  public static MussetaTesting get(Context context) {
    return (MussetaTesting) context.getApplicationContext();
  }

  public void setTestingMussetaComponent(MussetaTestingComponent testingMussetaComponent) {
    applicationComponent = testingMussetaComponent;
  }

  public MussetaTestingComponent getMussetaTestingComponent() {
    return (MussetaTestingComponent) applicationComponent;
  }

  public void restoreDefaulMussetaComponent() {
    applicationComponent = applicationComponentCopy;
  }
}
