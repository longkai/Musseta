package yuejia.liu.musseta.api.hn;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Qualifier for Hacker News.
 *
 * @author longkai
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface HackerNews {
}
