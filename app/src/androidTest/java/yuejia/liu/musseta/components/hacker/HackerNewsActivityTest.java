package yuejia.liu.musseta.components.hacker;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
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
import yuejia.liu.musseta.MussetaTesting;
import yuejia.liu.musseta.MussetaTestingRunner;
import yuejia.liu.musseta.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by longkai on 11/9/15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HackerNewsActivityTest {
  @Rule public IntentsTestRule<HackerNewsActivity> rule = new IntentsTestRule<>(HackerNewsActivity.class, true, false);

  @Mock HackerNewsApi hackerNewsApi;

  List<Item> testingItems;

  @Before public void setUp() throws Exception {
    SessionIdentifierGenerator generator = new SessionIdentifierGenerator();

    testingItems = new ArrayList<>();
    // TODO: 11/12/15 this result a null member NPE bug, the library should fix it..
    MockitoAnnotations.initMocks(this);
    // let' s start with 50 samples
    Long[] ids = new Long[50];
    when(hackerNewsApi.topStories()).thenReturn(Observable.just(ids));

    for (int i = 0; i < ids.length; i++) {
      ids[i] = Long.valueOf(i);
      testingItems.add(new Item.Builder().title(generator.nextSessionId()).url("http://longist.me/items/" + i).build());
      when(hackerNewsApi.item(ids[i])).thenReturn(testingItems.get(i));
    }

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

    // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
    // every test run. In this case all external Intents will be blocked.
    intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
  }

  @After public void tearDown() throws Exception {
    MussetaTestingRunner testRunner = (MussetaTestingRunner) InstrumentationRegistry.getInstrumentation();
    testRunner.removeHooks();
  }

  @Test public void testStartup() throws Exception {
    // since espresso will wait util ui thread is idle,
    // so the first time espresso attach it, the items loading is done.
    onView(withId(android.R.id.progress)).check(matches(not(isDisplayed())));
    onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
  }

  @Test public void testDoubleTapToTop() throws Exception {
    onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollToPosition(testingItems.size() - 1));
    // che the first one is not displayed
    onView(withText(testingItems.get(0).title)).check(doesNotExist());

    onView(withId(R.id.toolbar)).perform(doubleClick());

    // yet another way to check the first item matches
    assertThat(rule.getActivity().layoutManager.findFirstCompletelyVisibleItemPosition(), is(0));
  }

  @Test public void testTapItem() throws Exception {
    int position = (int) (Math.random() * testingItems.size());

    onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

    intended(allOf(
        hasAction(Intent.ACTION_VIEW),
        hasData(testingItems.get(position).url)
    ));
  }

  @Test public void testLongTapItem() throws Exception {
    int position = (int) (Math.random() * testingItems.size());
    String targetTitle = testingItems.get(position).title;

    onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(position, longClick()));

    intended(allOf(
        hasAction(Intent.ACTION_SEND),
        hasExtra(Intent.EXTRA_TITLE, targetTitle),
        hasType(rule.getActivity().getString(R.string.mime_text_plain))
    ));
  }

  public final class SessionIdentifierGenerator {
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
      return new BigInteger(130, random).toString(32);
    }
  }
}
