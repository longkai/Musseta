package yuejia.liu.musseta.components.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

import timber.log.Timber;
import yuejia.liu.musseta.R;

/**
 * Welcome view.
 */
public class SplashView extends View {
  private final String   slogan;
  private final Drawable frame;
  private final Drawable animator;

  private final int frameWidth;
  private final int frameHeight;

  private final int animatorWidth;
  private final int animatorHeight;
  private final int animatorLeft;

  private final Paint paint;
  private final float textSize;
  private final int   textColor;
  private final int   spacing;
  private final Rect  rect;

  private int textWidth;
  private int textHeight;

  private int           offset;
  private ValueAnimator clipAnimator;
  private int           alpha;
  private ValueAnimator alphaAnimator;

  private AnimatorSet animatorSet;

  public SplashView(Context context) {
    super(context);
    slogan = getResources().getString(R.string.slogan);
    frame = ContextCompat.getDrawable(context, R.mipmap.splash_frame_4x);
    animator = ContextCompat.getDrawable(context, R.mipmap.splash_animator_4x);

    final float density = getResources().getDisplayMetrics().density;
    final float originalFactor = 4f;
    frameWidth = (int) (frame.getIntrinsicWidth() / originalFactor * density);
    frameHeight = (int) (frame.getIntrinsicHeight() / originalFactor * density);

    animatorWidth = (int) (animator.getIntrinsicWidth() / originalFactor * density);
    animatorHeight = (int) (animator.getIntrinsicHeight() / originalFactor * density);

    animatorLeft = (int) (340 / originalFactor * density + getResources().getDimensionPixelSize(R.dimen.large));

    rect = new Rect();
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, getResources().getDisplayMetrics());
    spacing = getResources().getDimensionPixelSize(R.dimen.medium);
    paint.setTextSize(textSize);
    textColor = Color.parseColor("#999999");
    paint.setColor(textColor);

    if (supportsAnimator()) {
      offset = animatorHeight;

      clipAnimator = ObjectAnimator.ofInt(animatorHeight, 0);
      clipAnimator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
      clipAnimator.addUpdateListener(animation -> {
        offset= (int) animation.getAnimatedValue();
        Timber.d("offset %d", offset);
        invalidate();
      });
      clipAnimator.setDuration(SplashComponent.animation_millis);

      alphaAnimator = ObjectAnimator.ofInt(0, 255);
      alphaAnimator.addUpdateListener(animation -> {
        alpha = (int) animation.getAnimatedValue();
        Timber.d("alpha %d", alpha);
        invalidate();
      });
      alphaAnimator.setDuration(SplashComponent.slogan_millis);

      animatorSet = new AnimatorSet();
    } else {
      offset = 0;
      alpha = 255;
    }
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();

    int x = (getWidth() - frameWidth) >> 1;
    int y = (getHeight() - animatorHeight) >> 1;

    rect.set(x + animatorLeft, y, x + animatorLeft + animatorWidth, y + offset);
    canvas.clipRect(rect, Region.Op.DIFFERENCE);

    animator.setBounds(x + animatorLeft, y, x + animatorLeft + animatorWidth, y + animatorHeight);
    animator.draw(canvas);

    y = (getHeight() - frameHeight) >> 1;
    frame.setBounds(x, y, x + frameWidth, y + frameHeight);
    frame.draw(canvas);

    x = (getWidth() - textWidth) >> 1;
    y += animatorHeight + spacing;

    paint.setAlpha(alpha);
    canvas.drawText(slogan, 0, slogan.length(), x, y + textHeight, paint);

    canvas.restore();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    textWidth = (int) paint.measureText(slogan);
    paint.getTextBounds(slogan, 0, slogan.length(), rect);
    textHeight = rect.height();

    setMeasuredDimension(
        resolveSize(getResources().getDisplayMetrics().widthPixels, widthMeasureSpec),
        resolveSize(getResources().getDisplayMetrics().heightPixels, heightMeasureSpec));
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (supportsAnimator()) {
      animatorSet.playSequentially(alphaAnimator, clipAnimator);
      animatorSet.start();
    }
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (supportsAnimator() && animatorSet.isRunning()) {
      animatorSet.cancel();

      animatorSet.removeAllListeners();
      clearAnimator(alphaAnimator, clipAnimator);
    }
  }

  static boolean supportsAnimator() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
  }

  static void clearAnimator(ValueAnimator... animators) {
    for (int i = 0; i < animators.length; i++) {
      animators[i].removeAllUpdateListeners();
      animators[i].removeAllListeners();
    }
  }
}
