package yuejia.liu.musseta.components.settings;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.gms.analytics.Tracker;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.web.SimpleWebViewDelegate;
import yuejia.liu.musseta.components.web.WebActivity;
import yuejia.liu.musseta.ui.MussetaActivity;
import yuejia.liu.musseta.ui.ResourceManager;
import yuejia.liu.musseta.widgets.ListDividerItemDecorator;

/**
 * Settings UI.
 */
public class SettingsActivity extends MussetaActivity<SettingsComponent> {
  @Inject Tracker tracker;

  @Bind(R.id.toolbar) Toolbar toolbar;

  private ResourceManager resourceManager;

  @Override protected SettingsComponent setupActivityComponent() {
    return Musseta.get(this).getMussetaComponent().settingsComponent();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    resourceManager = new ResourceManager(this);
    setContentView(R.layout.activity_settings);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem os = menu.add(Menu.NONE, android.R.id.button1, Menu.NONE, "Open Source");
    os.setIcon(R.drawable.ic_code_white_24dp);
    MenuItemCompat.setShowAsAction(os, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.button1:
        WebActivity.startActivity(this, SimpleWebViewDelegate
            .newBuilder()
            .usePageTitle(true)
            .url("https://github.com/longkai/Musseta")
            .build());
        return true;
      case android.R.id.home:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public static class SettingsFragment extends PreferenceFragmentCompat {
    @Override public void onCreatePreferences(Bundle bundle, String s) {
      addPreferencesFromResource(R.xml.settings);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      getListView().addItemDecoration(new ListDividerItemDecorator(getContext()));
    }

    @Override public boolean onPreferenceTreeClick(Preference preference) {
      String title = String.valueOf(preference.getTitle());
      if (getString(R.string.settings_title_open_source).equals(title)) {
        getFragmentManager().beginTransaction()
            .replace(R.id.action_settings, new OpenSourceLicenseFragment())
            .addToBackStack(null)
            .commit();
        return true;
      }
      return super.onPreferenceTreeClick(preference);
    }
  }

  public static class OpenSourceLicenseFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      recyclerView = new RecyclerView(getContext());
      recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      return recyclerView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      recyclerView.addItemDecoration(new ListDividerItemDecorator(getContext()));
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.setAdapter(new OpenSourceAdapter());
    }

    @Override public void onStart() {
      super.onStart();
      ((SettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings_title_open_source);
    }

    @Override public void onDetach() {
      // restore the toolbar title
      ((SettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings);
      super.onDetach();
    }

    private static class OpenSourceAdapter extends RecyclerView.Adapter<OpenSourceViewHolder> implements View.OnClickListener {
      String[] titles = new String[]{
          "The Android Open Source Project",
          "Mockito",
          "Dagger 2",
          "RxAndroid",
          "Timber",
          "Butter Knife",
          "LeakCanary",
          "Picasso",
          "Retrofit",
          "OkHttp",
          "Material Palette",
          "Gradle Retrolambda Plugin",
      };

      String[] descs = new String[]{
          "Android is an open source software stack for a wide range of mobile devices and a corresponding open source project led by Google.",
          "Tasty mocking framework for unit tests in Java.",
          "A fast dependency injector for Android and Java.",
          "RxJava bindings for Android.",
          "A logger with a small, extensible API which provides utility on top of Android's normal Log class.",
          "Field and method binding for Android views.",
          "A memory leak detection library for Android and Java.",
          "A powerful image downloading and caching library for Android.",
          "A type-safe HTTP client for Android and Java.",
          "An HTTP & SPDY client for Android and Java applications.",
          "Brings you all the Android Material color palette.",
          "A gradle plugin for getting java lambda support in java 6, 7 and android.",
      };

      String[] urls = new String[]{
          "https://source.android.com",
          "http://mockito.org",
          "http://google.github.io/dagger/",
          "https://github.com/ReactiveX/RxAndroid",
          "https://github.com/square/leakcanary",
          "https://github.com/JakeWharton/timber",
          "http://jakewharton.github.io/butterknife/",
          "http://square.github.io/picasso/",
          "http://square.github.io/retrofit/",
          "http://square.github.io/okhttp/",
          "https://github.com/longkai/material-palette",
          "https://github.com/evant/gradle-retrolambda",
      };

      @Override public OpenSourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SettingsActivity activity = (SettingsActivity) parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.two_line_list_item, parent, false);
        int dimen = view.getResources().getDimensionPixelSize(R.dimen.medium);
        view.setPadding(dimen, dimen, dimen, dimen);
        view.setBackgroundResource(activity.resourceManager.getResourceId(R.attr.selectableItemBackground));
        return new OpenSourceViewHolder(view);
      }

      @Override public void onBindViewHolder(OpenSourceViewHolder holder, int position) {
        holder.title.setText(titles[position]);
        holder.desc.setText(descs[position]);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
      }

      @Override public int getItemCount() {
        return titles.length;
      }

      @Override public void onClick(View v) {
        int position = (int) v.getTag();
        // TODO: 12/16/15 ga
        WebActivity.startActivity(v.getContext(), SimpleWebViewDelegate.newBuilder()
            .title(titles[position])
            .url(urls[position])
            .subtitle(descs[position])
            .build()
        );
      }
    }

    static class OpenSourceViewHolder extends RecyclerView.ViewHolder {
      @Bind(android.R.id.text1) TextView title;
      @Bind(android.R.id.text2) TextView desc;

      public OpenSourceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }
    }
  }
}
