package yuejia.liu.musseta;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ViewAnimator;

/**
 * An simple swipe refresh recycler fragment.
 *
 * @author longkai
 */
public class MussetaRecyclerFragment extends Fragment {
  private ViewAnimator       rootView;
  private ProgressBar        progressBar;
  private RecyclerView       recyclerView;
  private SwipeRefreshLayout swipeRefreshLayout;

  private boolean recyclerShown;

  @Override public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    final Context context = getActivity();
    rootView = new ViewAnimator(context);
    rootView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    ));

    // first add progress bar in the center
    progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    );
    lp.gravity = Gravity.CENTER;
    progressBar.setLayoutParams(lp);
    rootView.addView(progressBar);

    // add swipe refresh layout wrap the recycler view
    swipeRefreshLayout = new SwipeRefreshLayout(context);
    swipeRefreshLayout.setLayoutParams(new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    ));

    // makes recycler view refreshable
    recyclerView = new RecyclerView(context);
    recyclerView.setLayoutParams(new SwipeRefreshLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    ));
    swipeRefreshLayout.addView(recyclerView);

    rootView.addView(swipeRefreshLayout);
    return rootView;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // TODO: compat with API level < 14
    swipeRefreshLayout.setEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ((MussetaActivity) getActivity()).inject(this);
  }

  @Override public void onDestroyView() {
    recyclerShown = false;
    recyclerView = null;
    progressBar = null;
    rootView = null;
    super.onDestroyView();
  }

  public RecyclerView getRecyclerView() {
    return recyclerView;
  }

  public SwipeRefreshLayout getSwipeRefreshLayout() {
    return swipeRefreshLayout;
  }

  public void setRecyclerShown(boolean shown) {
    setRecyclerShown(shown, true);
  }

  public void setRecyclerShown(boolean shown, boolean animate) {
    if (recyclerShown == shown) {
      return;
    }
    recyclerShown = shown;
    if (shown) {
      if (animate) {
        progressBar.startAnimation(AnimationUtils.loadAnimation(
            getActivity(), android.R.anim.fade_out));
        swipeRefreshLayout.startAnimation(AnimationUtils.loadAnimation(
            getActivity(), android.R.anim.fade_in));
      } else {
        progressBar.clearAnimation();
        swipeRefreshLayout.clearAnimation();
      }
      progressBar.setVisibility(View.GONE);
      swipeRefreshLayout.setVisibility(View.VISIBLE);
    } else {
      if (animate) {
        progressBar.startAnimation(AnimationUtils.loadAnimation(
            getActivity(), android.R.anim.fade_in));
        swipeRefreshLayout.startAnimation(AnimationUtils.loadAnimation(
            getActivity(), android.R.anim.fade_out));
      } else {
        progressBar.clearAnimation();
        swipeRefreshLayout.clearAnimation();
      }
      progressBar.setVisibility(View.VISIBLE);
      swipeRefreshLayout.setVisibility(View.GONE);
    }
  }
}
