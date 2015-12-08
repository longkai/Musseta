package yuejia.liu.musseta.ui;

import android.os.Parcelable;
import android.view.View;

/**
 * Makes it public for easier accessing.
 */
public interface ViewInstanceStateLifecycle {
  /** see {@link View#onSaveInstanceState()} */
  Parcelable onSaveInstanceState();

  /** see {@link android.view.View#onRestoreInstanceState(Parcelable)} */
  void onRestoreInstanceState(Parcelable state);
}
