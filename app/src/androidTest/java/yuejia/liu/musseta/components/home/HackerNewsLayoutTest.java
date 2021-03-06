package yuejia.liu.musseta.components.home;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit.RestAdapter;
import rx.Observable;
import yuejia.liu.musseta.DaggerMussetaTestingComponent;
import yuejia.liu.musseta.MussetaModules;
import yuejia.liu.musseta.MussetaTesting;
import yuejia.liu.musseta.MussetaTestingRunner;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.home.hacker.HackerNews;
import yuejia.liu.musseta.components.home.hacker.HackerNewsApi;
import yuejia.liu.musseta.components.home.hacker.HackerNewsLayout;
import yuejia.liu.musseta.components.home.hacker.Item;
import yuejia.liu.musseta.misc.NetworkWatcher;
import yuejia.liu.musseta.misc.RecyclerViewMatchers;
import yuejia.liu.musseta.misc.SessionIdentifierGenerator;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

/**
 * Hacker News layout which lives in {@link HomeActivity} tests.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HackerNewsLayoutTest {
  @Rule public final IntentsTestRule<HomeActivity> rule = new IntentsTestRule<>(HomeActivity.class, true, false);

  @Mock HackerNewsApi  hackerNewsApi;
  @Mock NetworkWatcher networkWatcher;

  SessionIdentifierGenerator generator = new SessionIdentifierGenerator();
  List<Item> testingItems;

  @Before public void setUp() throws Exception {
    testingItems = new ArrayList<>();
    // TODO: 11/12/15 this result a null member NPE bug, the library should fix it..
    MockitoAnnotations.initMocks(this);
    // let' s start with PER_PAGE samples
    Long[] ids = new Long[HackerNewsLayout.PER_PAGE];

    for (int i = 0; i < ids.length; i++) {
      ids[i] = Long.valueOf(i);
      testingItems.add(new Item.Builder().title(generator.nextSessionId()).url("http://longist.me/items/" + i).build());
      when(hackerNewsApi.item(ids[i])).thenReturn(testingItems.get(i));
    }
    when(hackerNewsApi.topStories()).thenReturn(Observable.just(ids));

    MussetaTestingRunner.get().addHooks((activity, bundle) -> {
      HomeActivity homeActivity = (HomeActivity) activity;
      HomeTestingComponent homeTestingComponent = MussetaTesting.get(activity).getMussetaTestingComponent()
          .homeComponent(new HomeModule(homeActivity) {
            @Override protected HackerNewsApi providesHackerNewsApi(@HackerNews RestAdapter restAdapter) {
              return hackerNewsApi;
            }
          });
      homeTestingComponent.injectTesting(this);
      homeActivity.setActivityComponent(homeTestingComponent);
    });
  }

  @After public void tearDown() throws Exception {
    MussetaTestingRunner.get().removeHooks();
    MussetaTesting.get(InstrumentationRegistry.getTargetContext()).restoreDefaultMussetaComponent();
  }

  @Test public void testStartup() throws Exception {
    rule.launchActivity(null);
    // since espresso will wait util ui thread is idle,
    // so the first time espresso attach it, the items loading is done.
    onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));
    onView(withId(android.R.id.progress)).check(matches(not(isDisplayed())));
    onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
  }

  @Test public void testTapItem() throws Exception {
    rule.launchActivity(null);
    intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    int position = (int) (Math.random() * testingItems.size());

    onView(withId(R.id.recycler_view))
        .perform(ViewActions.closeSoftKeyboard())
        .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

    intended(allOf(
        hasAction(Intent.ACTION_VIEW),
        hasData(testingItems.get(position).url)
    ));
  }

  @Test public void testNoNetwork() throws Exception {
    // before the activity launching, replace the app component
    MussetaTesting mussetaTesting = MussetaTesting.get(InstrumentationRegistry.getTargetContext());
    mussetaTesting.setTestingMussetaComponent(
        DaggerMussetaTestingComponent.builder()
            .applicationModule(new MussetaModules.ApplicationModule(mussetaTesting))
            .networkModule(new MussetaModules.NetworkModule() {
              @Override protected NetworkWatcher providesNetworkWatcher(Application application) {
                return networkWatcher;
              }
            })
            .build()
    );

    when(networkWatcher.hasNetwork()).thenReturn(false);

    // after the activity launching, the replaced network watcher should be the mocked one
    rule.launchActivity(null);

    // no connection
    TimeUnit.SECONDS.sleep(1); // hands on... not that quickly...
    onView(withText(R.string.network_error)).check(matches(isDisplayed()));
  }

  @Test public void testRetry() throws Exception {
    // keep the scene
    testNoNetwork();

//    reset(networkWatcher); // no need to reset
    when(networkWatcher.hasNetwork()).thenReturn(true);

//    TimeUnit.SECONDS.sleep(1); // NOTE: sometimes seconds is enough to sync the snackbar item to > 90% visibility

    onView(withText(R.string.retry)).perform(click()).check(doesNotExist());

    onView(withId(android.R.id.empty))
        .check(matches(not(isDisplayed())));
  }

  @Test public void testClickReplyCounting() throws Exception {
    rule.launchActivity(null);
    intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    final int testingPosition = 7;

    onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(testingPosition, new ViewAction() {
      @Override public Matcher<View> getConstraints() {
        return null;
      }

      @Override public String getDescription() {
        return "click recycler view' s child";
      }

      @Override public void perform(UiController uiController, View view) {
        view.findViewById(R.id.reply_counting).performClick();
      }
    }));

    intended(allOf(
        hasAction(Intent.ACTION_VIEW),
        hasData("https://news.ycombinator.com/item?id=" + testingItems.get(testingPosition).id)
    ));
  }

  @Test public void testRefresh() throws Exception {
    // reset
    Long[] ids = new Long[500];

    for (int i = 0; i < ids.length; i++) {
      ids[i] = Long.valueOf(i);
      testingItems.add(new Item.Builder().title(generator.nextSessionId()).url("http://longist.me/items/" + i).build());
      when(hackerNewsApi.item(ids[i])).thenReturn(testingItems.get(i));
    }
    when(hackerNewsApi.topStories()).thenReturn(Observable.just(ids));

    rule.launchActivity(null);

    // perform a loading...
    onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollToPosition(HackerNewsLayout.PER_PAGE - HackerNewsLayout.PRE_LOADING_OFFSET + 2));

    TimeUnit.SECONDS.sleep(1); // wait...

    // check has loading more data
    onView(withId(R.id.recycler_view)).check(matches(RecyclerViewMatchers.hasCount(RecyclerViewMatchers.LARGER_THAN, HackerNewsLayout.PER_PAGE)));

    // swipe to refresh...
    onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollToPosition(0));
    onView(withId(R.id.refresh_layout)).perform(ViewActions.swipeDown());

    TimeUnit.SECONDS.sleep(1); // wait...

    onView(withId(R.id.recycler_view)).check(matches(RecyclerViewMatchers.hasCount(RecyclerViewMatchers.AT_MOST, HackerNewsLayout.PER_PAGE)));
  }
}
