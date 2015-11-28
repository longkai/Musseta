package yuejia.liu.musseta.components.settings;

import javax.inject.Inject;

import android.content.SharedPreferences;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.widget.SwitchCompat;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import yuejia.liu.musseta.MussetaTesting;
import yuejia.liu.musseta.MussetaTestingRunner;
import yuejia.liu.musseta.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

/**
 * Created by longkai on 11/27/15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityTest {
  @Rule public final ActivityTestRule<SettingsActivity> rule = new ActivityTestRule<>(SettingsActivity.class, true, false);

  @Inject SharedPreferences preferences;

  @Before public void setUp() throws Exception {
    MussetaTestingRunner.get().addHooks((activity, bundle) -> {
      SettingsActivity settingsActivity = (SettingsActivity) activity;
      SettingsTestingComponent testingComponent = MussetaTesting.get(activity).getMussetaTestingComponent().plus();

      testingComponent.injectTesting(this);

      settingsActivity.setActivityComponent(testingComponent);
    });
  }

  @After public void tearDown() throws Exception {
    MussetaTestingRunner.get().removeHooks();
  }

  @Test public void testLaidOut() throws Exception {
    rule.launchActivity(null);

    onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.settings))));

    onView(withText(R.string.settings_title_enable_tracking)).check(matches(isCompletelyDisplayed()));
    onView(withText(R.string.settings_summary_enable_tracking)).check(matches(isCompletelyDisplayed()));
  }

  @Test public void testToggleCheckGA() throws Exception {
    SettingsActivity activity = rule.launchActivity(null);

    // for more information, such as where the id come from, check the preference-v7 source
    onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(R.string.settings_title_enable_tracking)), click()));

    boolean checked = preferences.getBoolean(activity.getString(R.string.settings_key_enable_tracking), activity.getResources().getBoolean(R.bool.settings_def_enable_tracking));

    onView(withClassName(is(SwitchCompat.class.getName()))).check(matches(checked ? isChecked() : isNotChecked()));

    // restore default
    SharedPreferencesCompat.EditorCompat.getInstance()
        .apply(preferences.edit().putBoolean(activity.getString(R.string.settings_key_enable_tracking), !checked));
  }
}
