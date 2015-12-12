package yuejia.liu.musseta.misc;

/**
 * Some math static helper methods.
 */
public class MathHelper {
  public static int max(int[] a) {
    int max = a[0];
    for (int i = 1; i < a.length; i++) {
      if (a[i] > max) {
        max = a[i];
      }
    }
    return max;
  }

  public static int min(int[] a) {
    int min = a[0];
    for (int i = 1; i < a.length; i++) {
      if (a[i] < min) {
        min = a[i];
      }
    }
    return min;
  }
}
