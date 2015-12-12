package yuejia.liu.musseta.components.home.dribbble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import yuejia.liu.musseta.R;

/**
 * Dribble logo with a background.
 */
public class LoadingDrawable extends Drawable {
  private final Paint    paint;
  private final Drawable logo;


  private final float density;
  private final int   backgroundColor;
  private final int   width;
  private final int   height;

  public LoadingDrawable(Context context) {
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    logo = ContextCompat.getDrawable(context, R.mipmap.dribbble);

    density = context.getResources().getDisplayMetrics().density;

    width = context.getResources().getDisplayMetrics().widthPixels;
    height = (int) (width / (4f / 3));

    backgroundColor = Color.parseColor("#ECECEC");
  }

  @Override public void draw(Canvas canvas) {
    Rect bounds = getBounds();
    paint.setColor(backgroundColor);
    canvas.drawRect(bounds, paint);


    // the original size is 300x300 4x, we need to resize it to adapt for current device
    int width = (int) (logo.getIntrinsicWidth() / 4 * density);
    int height = (int) (logo.getIntrinsicHeight() / 4 * density);
    int left = bounds.centerX() - width / 2;
    int top = bounds.centerY() - height / 2;
    logo.setBounds(left, top, left + width, top + height);
    logo.draw(canvas);
  }

  @Override public void setAlpha(int alpha) {

  }

  @Override public void setColorFilter(ColorFilter colorFilter) {

  }

  @Override public int getOpacity() {
    return 0;
  }

  @Override public int getIntrinsicWidth() {
    return width;
  }

  @Override public int getIntrinsicHeight() {
    return height;
  }
}
