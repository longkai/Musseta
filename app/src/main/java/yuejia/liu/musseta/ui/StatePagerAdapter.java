package yuejia.liu.musseta.ui;

import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple keeping-state view pager adapter which keeping the state of its page view.
 */
public abstract class StatePagerAdapter extends PagerAdapter {
  private ParcelableSparseArray                         states = new ParcelableSparseArray();
  private SparseArrayCompat<ViewInstanceStateLifecycle> views  = new SparseArrayCompat<>();

  @Override public Parcelable saveState() {
    for (int i = 0; i < views.size(); i++) {
      int key = views.keyAt(i);
      ViewInstanceStateLifecycle lifecycle = views.get(key);
      Parcelable parcelable = lifecycle.onSaveInstanceState();
      states.put(key, parcelable);
    }
    views.removeAtRange(0, views.size());
    return states;
  }

  @Override public void restoreState(Parcelable state, ClassLoader loader) {
    states = (ParcelableSparseArray) state;
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    ViewInstanceStateLifecycle lifecycle = views.get(position);
    if (lifecycle != null) {
      Parcelable parcelable = lifecycle.onSaveInstanceState();
      states.put(position, parcelable);

      views.delete(position);
    }

    container.removeView((View) object);
  }

  /** don' t call {@link ViewGroup#addView(View)}, or there would no restore saved instance! */
  public abstract View instantiateView(ViewGroup container, int position);

  @Override public Object instantiateItem(ViewGroup container, int position) {
    View view = instantiateView(container, position);
    if (view instanceof ViewInstanceStateLifecycle) {
      ViewInstanceStateLifecycle lifecycle = (ViewInstanceStateLifecycle) view;

      Parcelable parcelable = states.get(position);
      if (parcelable != null) {
        lifecycle.onRestoreInstanceState(parcelable);
      }

      views.put(position, lifecycle);
    }

    // since addView will makes the view thr layout process, so we must restore the view state before calling addView
    container.addView(view);
    return view;
  }
}
