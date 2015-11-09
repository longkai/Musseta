package yuejia.liu.musseta.components.hacker;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit.RestAdapter;
import rx.Observable;
import yuejia.liu.musseta.MussetaTestingRunner;
import yuejia.liu.musseta.MussetaTesting;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

/**
 * Created by longkai on 11/9/15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HackerNewsActivityTest {
  @Rule public ActivityTestRule<HackerNewsActivity> rule = new ActivityTestRule<>(HackerNewsActivity.class, true, false);

  @Mock HackerNewsApi hackerNewsApi;

  Item startupItem; // single item for startup

  @Before public void setUp() throws Exception {
    startupItem = new Item();
    startupItem.title = String.valueOf(System.currentTimeMillis());

    MockitoAnnotations.initMocks(this);
    when(hackerNewsApi.topStories()).thenReturn(Observable.just(new Long[]{Long.MIN_VALUE}));
    when(hackerNewsApi.item(Long.MIN_VALUE)).thenReturn(startupItem);

    MussetaTestingRunner testRunner = (MussetaTestingRunner) InstrumentationRegistry.getInstrumentation();

    testRunner.addHooks((activity, bundle) -> {
      HackerNewsActivity hackerNewsActivity = (HackerNewsActivity) activity;
      HackerNewsTestingComponent hackerNewsTestingComponent = MussetaTesting.get(activity).getMussetaTestingComponent()
          .plus(new HackerNewsModule(hackerNewsActivity) {
            @Override public HackerNewsApi providesHackerNewsApi(@HackerNews RestAdapter restAdapter) {
              return hackerNewsApi;
            }
          });
      hackerNewsTestingComponent.injectTesting(HackerNewsActivityTest.this);
      hackerNewsActivity.setActivityComponent(hackerNewsTestingComponent);
    });

    // launch it, boom!
    rule.launchActivity(null);
  }

  @After public void tearDown() throws Exception {
    MussetaTestingRunner testRunner = (MussetaTestingRunner) InstrumentationRegistry.getInstrumentation();
    testRunner.removeHooks();
  }

  @Test public void testStartup() throws Exception {
    // since espresso will wait util ui thread is idle,
    // so the first time espresso attach it, the items loading is done.
    onView(withId(android.R.id.progress)).check(matches(not(isDisplayed())));
    onView(withText(startupItem.title)).check(matches(isDisplayed()));
  }
}
