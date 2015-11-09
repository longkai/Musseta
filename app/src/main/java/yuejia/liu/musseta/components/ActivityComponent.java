package yuejia.liu.musseta.components;

/**
 * A simple activity component for sub activity to extend.
 */
public interface ActivityComponent<T> {
  T inject(T activity);
}
