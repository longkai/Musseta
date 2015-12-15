package yuejia.liu.musseta.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Simple native list divider.
 * <p/>
 * // TODO: 11/8/15 add orientation and margin support in need!
 */
public class ListDividerItemDecorator extends RecyclerView.ItemDecoration {
  private static int[] ATTRS = new int[]{android.R.attr.listDivider};

  private final Drawable divider;

  public ListDividerItemDecorator(Context context) {
    TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
    divider = typedArray.getDrawable(0);
    typedArray.recycle();
  }

  @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDrawOver(c, parent, state);
    int height = divider.getIntrinsicHeight();
    final int count = parent.getChildCount();
    for (int i = 0; i < count; i++) {
      View view = parent.getChildAt(i);
      divider.setBounds(view.getLeft(), view.getBottom(), view.getRight(), view.getBottom() + height);
      divider.draw(c);
    }
  }
}
