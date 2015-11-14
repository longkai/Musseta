package yuejia.liu.musseta.ui;

/**
 * {@link android.support.v7.widget.RecyclerView} item touch helper methods.
 */
public interface ItemTouchHelperAdapter {
  void onItemMove(int fromPosition, int toPosition);

  void onItemDismiss(int position);
}
