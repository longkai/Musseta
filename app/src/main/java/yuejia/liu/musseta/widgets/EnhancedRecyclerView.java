package yuejia.liu.musseta.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * An enhanced recycler view which support an empty view.
 */
public class EnhancedRecyclerView extends RecyclerView {
  private View    emptyView;
  private boolean registered;

  private AdapterDataObserver observer = new AdapterDataObserver() {
    @Override public void onItemRangeInserted(int positionStart, int itemCount) {
      super.onItemRangeInserted(positionStart, itemCount);
      updateEmptyStatus(isEmpty());
    }

    @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
      super.onItemRangeRemoved(positionStart, itemCount);
      updateEmptyStatus(isEmpty());
    }
  };

  public EnhancedRecyclerView(Context context) {
    super(context);
  }

  public EnhancedRecyclerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public EnhancedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override public void setAdapter(Adapter adapter) {
    unregisterIfAny();

    super.setAdapter(adapter);

    register(adapter);
  }

  @Override protected void onDetachedFromWindow() {
    unregisterIfAny();
    super.onDetachedFromWindow();
  }

  private void register(Adapter adapter) {
    adapter.registerAdapterDataObserver(observer);
    registered = true;
  }

  private void unregisterIfAny() {
    if (registered) {
      getAdapter().unregisterAdapterDataObserver(observer);
      registered = false;
    }
  }

  public View getEmptyView() {
    return emptyView;
  }

  public void setEmptyView(View emptyView) {
    this.emptyView = emptyView;
  }

  public boolean isEmpty() {
    Adapter adapter = getAdapter();
    return adapter == null ? true : adapter.getItemCount() == 0;
  }

  private void updateEmptyStatus(boolean empty) {
    if (empty) {
      setVisibility(GONE);
      if (emptyView != null) {
        emptyView.setVisibility(VISIBLE);
      }
    } else {
      setVisibility(VISIBLE);
      if (emptyView != null) {
        emptyView.setVisibility(GONE);
      }
    }
  }
}
