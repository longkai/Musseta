package yuejia.liu.musseta.misc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.support.annotation.IntDef;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;

/**
 * Some utility methods for easier {@link RecyclerView} testing.
 */
public class RecyclerViewMatchers {
  public static final int AT_MOST     = 0;
  public static final int AT_LEAST    = 1;
  public static final int LARGER_THAN = 2;
  public static final int LESS_THAN   = 3;
  public static final int EXACTLY     = 4;

  @IntDef({AT_MOST, AT_LEAST, LARGER_THAN, LESS_THAN, EXACTLY})
  @Retention(RetentionPolicy.SOURCE)
  public @interface CountingType {
  }

  public static BoundedMatcher<View, RecyclerView> hasCount(@CountingType int type, int count) {
    return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
      @Override protected boolean matchesSafely(RecyclerView item) {
        int itemCount = item.getAdapter().getItemCount();
        switch (type) {
          case AT_MOST:
            return itemCount <= count;
          case AT_LEAST:
            return itemCount >= count;
          case LARGER_THAN:
            return itemCount > count;
          case LESS_THAN:
            return itemCount < count;
          case EXACTLY:
            return itemCount == count;
          default:
            throw new IllegalArgumentException("No such type " + type);
        }
      }

      @Override public void describeTo(Description description) {
        String optr;
        switch (type) {
          case AT_MOST:
            optr = "<=";
            break;
          case AT_LEAST:
            optr = ">=";
            break;
          case LARGER_THAN:
            optr = ">";
            break;
          case LESS_THAN:
            optr = "<";
            break;
          case EXACTLY:
            optr = "==";
            break;
          default:
            throw new IllegalArgumentException("No such type " + type);
        }
        description.appendText(String.format("has count %s %d", optr, count));
      }
    };
  }
}
