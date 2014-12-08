package yuejia.liu.musseta;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Musseta base list fragment.
 *
 * @author longkai
 */
public class MussetaListFragment extends ListFragment {
  private SwipeRefreshLayout swipeRefreshLayout;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Create the list fragment's content view by calling the super method
    final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);

    // Now create a SwipeRefreshLayout to wrap the fragment's content view
    swipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());

    // Add the list fragment's content view to the SwipeRefreshLayout, making sure that it fills
    // the SwipeRefreshLayout
    swipeRefreshLayout.addView(listFragmentView,
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    // Make sure that the SwipeRefreshLayout will fill the fragment
    swipeRefreshLayout.setLayoutParams(
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));

    // Now return the SwipeRefreshLayout as this fragment's content view
    return swipeRefreshLayout;

  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ((MussetaActivity) getActivity()).inject(this);
  }

  public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
    swipeRefreshLayout.setOnRefreshListener(listener);
  }

  public boolean isRefreshing() {
    return swipeRefreshLayout.isRefreshing();
  }

  public void setRefreshing(boolean refreshing) {
    swipeRefreshLayout.setRefreshing(refreshing);
  }

  public void setColorScheme(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
    swipeRefreshLayout.setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4);
  }

  public SwipeRefreshLayout getSwipeRefreshLayout() {
    return swipeRefreshLayout;
  }

  private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

    public ListFragmentSwipeRefreshLayout(Context context) {
      super(context);
    }

    @Override public boolean canChildScrollUp() {
      final ListView listView = getListView();
      if (listView.getVisibility() == View.VISIBLE) {
        return canListViewScrollUp(listView);
      } else {
        return false;
      }
    }
  }

  private static boolean canListViewScrollUp(ListView listView) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      // For ICS and above we can call canScrollVertically() to determine this
      return ViewCompat.canScrollVertically(listView, -1);
    } else {
      // Pre-ICS we need to manually check the first visible item and the child view's top
      // value
      return listView.getChildCount() > 0 &&
          (listView.getFirstVisiblePosition() > 0
              || listView.getChildAt(0).getTop() < listView.getPaddingTop());
    }
  }
}
