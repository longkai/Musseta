package yuejia.liu.musseta.components.home.product;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import timber.log.Timber;

/**
 * A simple char center in circle with a backgrounded image loading place holder for product hunt scenarios.
 */
public class LoadingDrawable extends Drawable {
  private static final int DEFAULT_TEXT_SIZE = 20; // dp, all the same
  private static final int DEFAULT_RADIUS    = 30;
  private static final int DEFAULT_WIDTH     = 168;
  private static final int DEFAULT_HEIGHT    = 118;

  private String         text;
  private float          textSize; // px, all the same
  private int            textColor;
  private int            circleColor;
  private int            radius;
  private int            backgroundColor;
  private DisplayMetrics displayMetrics;

  private int intrinsicWidth;
  private int intrinsicHeight;

  private final Paint paint;
  private final Rect  textBound;

  private LoadingDrawable(Builder builder) {
    textColor = builder.textColor;
    text = builder.text;
    circleColor = builder.circleColor;
    backgroundColor = builder.backgroundColor;
    displayMetrics = builder.displayMetrics;

    intrinsicWidth = displayMetrics.widthPixels;
    intrinsicHeight = (int) (intrinsicWidth * 1f / (DEFAULT_WIDTH / DEFAULT_HEIGHT));

    final float ratio = intrinsicWidth / DEFAULT_WIDTH;
    textSize = ratio * DEFAULT_TEXT_SIZE;
    radius = (int) (ratio * DEFAULT_RADIUS);

    if (text == null) {
      text = "P";
    }

    if (textColor == 0) {
      textColor = Color.WHITE;
    }

    if (circleColor == 0) {
      circleColor = Color.parseColor("#D8D8D8");
    }

    if (backgroundColor == 0) {
      backgroundColor = Color.parseColor("#ECECEC");
    }

    Timber.d("w %d, h %d, radius %d, textsize %.1f, ratio %.1f", intrinsicWidth, intrinsicHeight, radius, textSize, ratio);

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textBound = new Rect();
  }

  @Override public void draw(Canvas canvas) {
    Rect bounds = getBounds();
    paint.setColor(backgroundColor);
    canvas.drawRect(bounds, paint);

    paint.setColor(circleColor);
    canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, paint);

    paint.setTextSize(textSize);
    paint.getTextBounds(text, 0, text.length(), textBound);
    int textHeight = textBound.height();
    float textWidth = paint.measureText(text);
    paint.setTypeface(Typeface.MONOSPACE);
    paint.setColor(textColor);
    canvas.drawText(text, 0, text.length(), bounds.centerX() - (textWidth / 2), bounds.centerY() + (textHeight / 2), paint);
  }

  @Override public void setAlpha(int alpha) {

  }

  @Override public void setColorFilter(ColorFilter colorFilter) {

  }

  @Override public int getOpacity() {
    return 0;
  }

  @Override public int getIntrinsicHeight() {
    return intrinsicHeight;
  }

  @Override public int getIntrinsicWidth() {
    return intrinsicWidth;
  }

  public static final class Builder {
    private int            textColor;
    private String         text;
    private int            circleColor;
    private int            backgroundColor;
    private DisplayMetrics displayMetrics;

    public Builder() {}

    public Builder textColor(int val) {
      textColor = val;
      return this;
    }

    public Builder text(String val) {
      text = val;
      return this;
    }

    public Builder circleColor(int val) {
      circleColor = val;
      return this;
    }

    public Builder backgroundColor(int val) {
      backgroundColor = val;
      return this;
    }

    public Builder displayMetrics(DisplayMetrics val) {
      displayMetrics = val;
      return this;
    }

    public LoadingDrawable build() {return new LoadingDrawable(this);}
  }
}
