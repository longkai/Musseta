package yuejia.liu.musseta.components.hacker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import yuejia.liu.musseta.R;

/**
 * Show a counting in the replay outline.
 */
public class ReplyCountingView extends View {
  // the default outline a little smaller than the large counting number, so...
  private static final float SCALE_FACTOR = 1.25f;

  private int      counting;
  private Paint    paint;
  private Rect     rect;
  private Drawable replayOutline;

  public ReplyCountingView(Context context) {
    this(context, null);
  }

  public ReplyCountingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    bootstrap(context, attrs, 0, 0);
  }

  private void bootstrap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    replayOutline = ContextCompat.getDrawable(context, R.mipmap.ic_chat_bubble_outline_black_24dp);
    replayOutline = DrawableCompat.wrap(replayOutline);
    DrawableCompat.setTint(replayOutline, ContextCompat.getColor(context, R.color.material_grey_400));

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setTypeface(Typeface.DEFAULT_BOLD);
    paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, getResources().getDisplayMetrics()));
    paint.setColor(ContextCompat.getColor(context, R.color.material_grey_400));

    rect = new Rect();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(replayOutline.getIntrinsicWidth() + getPaddingLeft() + getPaddingRight(),
        replayOutline.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom());
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();

    int replayOutlineIntrinsicWidth = (int) (replayOutline.getIntrinsicWidth() * SCALE_FACTOR);
    int replayOutlineIntrinsicHeight = (int) (replayOutline.getIntrinsicHeight() * SCALE_FACTOR);

    int left = (getWidth() - replayOutlineIntrinsicWidth) >> 1;
    int top = (getHeight() - replayOutlineIntrinsicHeight) >> 1;
    replayOutline.setBounds(left, top, left + replayOutlineIntrinsicWidth, top + replayOutlineIntrinsicHeight);
    replayOutline.draw(canvas);

    String text = String.valueOf(counting);
    paint.getTextBounds(text, 0, text.length(), rect);

    int x = (getWidth() - rect.width()) >> 1;
    int y = (getHeight() - rect.height()) >> 1;

    // assume the array take up 18% of the outline...
    canvas.drawText(text, 0, text.length(), x, getTextYBaseLine(paint, rect) + y - replayOutlineIntrinsicHeight * .18f, paint);

    canvas.restore();
  }

  public int getCounting() {
    return counting;
  }

  public void setCounting(int counting) {
    if (this.counting != counting) {
      this.counting = counting;
      invalidate();
    }
  }

  static float getTextYBaseLine(Paint paint, Rect rect) {
    return ((paint.descent() - paint.ascent()) - (rect.bottom - rect.top)) / 2 - rect.top;
  }
}
