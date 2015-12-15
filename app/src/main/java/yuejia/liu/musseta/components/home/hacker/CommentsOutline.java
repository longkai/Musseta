package yuejia.liu.musseta.components.home.hacker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import yuejia.liu.musseta.R;

/**
 * Chat outline with a comments count.
 */
public class CommentsOutline extends Drawable {
  private final int count;
  private final Drawable drawable;

  private final Rect rect;
  private final Paint paint;

  public CommentsOutline(Context context, int count) {
    this.count = count;
    drawable = ContextCompat.getDrawable(context, R.drawable.ic_comment_outline_24dp);

    rect = new Rect();
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, context.getResources().getDisplayMetrics()));

    paint.setColor(Color.WHITE);
  }

  @Override public void draw(Canvas canvas) {
    Rect bounds = getBounds();
    drawable.setBounds(bounds);
    drawable.draw(canvas);

    String txt = String.valueOf(count);
    paint.getTextBounds(txt, 0, txt.length(), rect);
    int textHeight = rect.height();
    int textWidth = (int) paint.measureText(txt);

    canvas.drawText(txt, 0, txt.length(), bounds.centerX() - textWidth / 2, bounds.centerY() + textHeight / 2, paint);
  }

  @Override public void setAlpha(int alpha) {

  }

  @Override public void setColorFilter(ColorFilter colorFilter) {

  }

  @Override public int getOpacity() {
    return 0;
  }

  @Override public int getIntrinsicHeight() {
    return drawable.getIntrinsicHeight();
  }

  @Override public int getIntrinsicWidth() {
    return drawable.getIntrinsicWidth();
  }
}
