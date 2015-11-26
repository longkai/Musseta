package yuejia.liu.musseta.components.hacker;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit.RestAdapter;
import rx.Observable;
import timber.log.Timber;
import yuejia.liu.musseta.DaggerMussetaTestingComponent;
import yuejia.liu.musseta.MussetaModules;
import yuejia.liu.musseta.MussetaTesting;
import yuejia.liu.musseta.MussetaTestingRunner;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.settings.SettingsActivity;
import yuejia.liu.musseta.misc.NetworkWatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
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

  @Mock HackerNewsApi  hackerNewsApi;
  @Mock NetworkWatcher networkWatcher;

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

    MussetaTestingRunner.get().addHooks((activity, bundle) -> {
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
  }

  @After public void tearDown() throws Exception {
    MussetaTestingRunner.get().removeHooks();
    MussetaTesting.get(InstrumentationRegistry.getTargetContext()).restoreDefaultMussetaComponent();
  }

  @Test public void testStartup() throws Exception {
    rule.launchActivity(null);
    // since espresso will wait util ui thread is idle,
    // so the first time espresso attach it, the items loading is done.
    onView(withId(android.R.id.progress)).check(matches(not(isDisplayed())));
    onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
  }

  @Test public void testDoubleTapToTop() throws Exception {
    rule.launchActivity(null);

    onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollToPosition(testingItems.size() - 1));
    // che the first one is not displayed
    onView(withText(testingItems.get(0).title)).check(doesNotExist());

    onView(withId(R.id.toolbar)).perform(doubleClick());

    // yet another way to check the first item matches
    assertThat(rule.getActivity().layoutManager.findFirstCompletelyVisibleItemPosition(), is(0));
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

  @Test public void testLongTapItem() throws Exception {
    rule.launchActivity(null);
    intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    int position = (int) (Math.random() * testingItems.size());
    String targetTitle = testingItems.get(position).title;

    Espresso.closeSoftKeyboard();
    TimeUnit.SECONDS.sleep(1);

    try {
      onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(position, longClick()));

      intended(allOf(
          hasAction(Intent.ACTION_SEND),
          hasExtra(Intent.EXTRA_TITLE, targetTitle),
          hasType(rule.getActivity().getString(R.string.mime_text_plain))
      ));
    } catch (Exception ex) {
      // TODO: 11/15/15 wait AOSP to fix it
      Timber.wtf(ex, "should not happen Injecting to another application requires INJECT_EVENTS permission");
    }
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
    onView(withText(R.string.network_problem)).check(matches(isDisplayed()));
  }

  @Test public void testRetry() throws Exception {
    // keep the scene
    testNoNetwork();

//    reset(networkWatcher); // no need to reset
    when(networkWatcher.hasNetwork()).thenReturn(true);

//    TimeUnit.SECONDS.sleep(1); // NOTE: sometimes seconds is enough to sync the snackbar item to > 90% visibility

    onView(withText(R.string.retry)).perform(click()).check(doesNotExist());

    assertThat(rule.getActivity().layoutManager.getItemCount(), Matchers.greaterThan(0));
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

  @Test public void testOpenSettingsUI() throws Exception {
    HackerNewsActivity activity = rule.launchActivity(null);

    intending(isInternal()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

    openActionBarOverflowOrOptionsMenu(activity);

    onView(withText(R.string.settings)).perform(click());

    intended(allOf(
        toPackage(activity.getPackageName()),
        hasComponent(SettingsActivity.class.getName())
    ));
  }

  public static final class SessionIdentifierGenerator {
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
      return new BigInteger(130, random).toString(32);
    }
  }
}
