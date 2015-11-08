package yuejia.liu.musseta.components.hacker;

import java.util.ArrayList;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.ui.MussetaActivity;
import yuejia.liu.musseta.ui.ResourceManager;
import yuejia.liu.musseta.widgets.ListDividerItemDecorator;


/**
 * Hacker News ui.
 */
public class HackerNewsActivity extends MussetaActivity implements SwipeRefreshLayout.OnRefreshListener {
  private static final String KEY_PREVIOUS_POSITION = "key_previous_position";
  private static final String KEY_HACKER_NEWS_ITEMS = "key_hacker_news_items";

  @Inject HackerNewsPresenter   presenter;
  @Inject ResourceManager       resourceManager;
  @Inject CompositeSubscription subscriptions;

  @Bind(R.id.toolbar)          Toolbar            toolbar;
  @Bind(android.R.id.progress) ProgressBar        progress;
  @Bind(R.id.recycler_view)    RecyclerView       recyclerView;
  @Bind(R.id.refresh_layout)   SwipeRefreshLayout refreshLayout;

  HackerNewsAdapter        hackerNewsAdapter;
  LinearLayoutManager      layoutManager;
  ListDividerItemDecorator dividerItemDecorator;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_hacker_news);
    ButterKnife.bind(this);

    setupViews();

    if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_HACKER_NEWS_ITEMS)) {
      presenter.present();
    } else {
      ArrayList<Item> items = savedInstanceState.getParcelableArrayList(KEY_HACKER_NEWS_ITEMS);
      appendAll(items);
      int previousPosition = savedInstanceState.getInt(KEY_PREVIOUS_POSITION);
      if (previousPosition != RecyclerView.NO_POSITION) {
        layoutManager.scrollToPosition(previousPosition);
      }
    }

    Timber.d("bootstrapped...");
  }

  @Override protected void setupActivityComponent() {
    Musseta.get(this).getMussetaComponent().plus(new HackerNewsModule(this)).inject(this);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // if we use layout manager, at some point, the layout manager may not attach to the adapter
    if (hackerNewsAdapter.items.size() > 0) {
      outState.putParcelableArrayList(KEY_HACKER_NEWS_ITEMS, hackerNewsAdapter.items);
      outState.putInt(KEY_PREVIOUS_POSITION, layoutManager.findFirstVisibleItemPosition());
    }
  }

  @Override protected void onDestroy() {
    recyclerView.removeItemDecoration(dividerItemDecorator);
    subscriptions.unsubscribe();
    super.onDestroy();
  }

  @Override public void onRefresh() {
    hackerNewsAdapter.empty();
    presenter.refresh();
  }

  // presenter method
  void showRefreshing(boolean refreshing) {
    if (refreshLayout.isRefreshing() != refreshing) {
      refreshLayout.setRefreshing(refreshing);
    }
  }

  void showLoading(boolean loading) {
    progress.setVisibility(loading ? View.VISIBLE : View.GONE);
  }

  void setupViews() {
    setupToolbar();

    refreshLayout.setColorSchemeResources(
        R.color.material_amber_400,
        R.color.material_light_blue_400,
        R.color.material_pink_400,
        R.color.material_deep_purple_400
    );
    refreshLayout.setOnRefreshListener(this);
    // TODO: 11/8/15 future feature
    refreshLayout.setEnabled(false);

    recyclerView.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    dividerItemDecorator = new ListDividerItemDecorator(this);
    recyclerView.addItemDecoration(dividerItemDecorator);

    hackerNewsAdapter = new HackerNewsAdapter();
    recyclerView.setAdapter(hackerNewsAdapter);
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);
    final GestureDetectorCompat detectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
      @Override public boolean onDoubleTap(MotionEvent e) {
        recyclerView.smoothScrollToPosition(0);
        return true;
      }
    });
    toolbar.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        return detectorCompat.onTouchEvent(event);
      }
    });
  }

  void appendAll(ArrayList<Item> items) {
    hackerNewsAdapter.appendAll(items);
  }

  void appendItem(Item item) {
    hackerNewsAdapter.append(item);
  }
  // presenter

  static class HackerNewsAdapter extends RecyclerView.Adapter<HackerNewsViewHolder> {
    private final ArrayList<Item> items = new ArrayList<>();

    @Override public HackerNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new HackerNewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override public void onBindViewHolder(HackerNewsViewHolder holder, int position) {
      Item item = items.get(position);
      holder.title.setText(item.title);
      holder.title.setTag(item);

      int resourceId = ((HackerNewsActivity) holder.title.getContext()).resourceManager.getResourceId(R.attr.selectableItemBackground);
      holder.title.setBackgroundResource(resourceId);
    }

    public void append(Item item) {
      items.add(item);
      notifyItemInserted(getItemCount() - 1);
    }

    public void appendAll(ArrayList<Item> items) {
      int begin = this.items.size();
      this.items.addAll(items);
      notifyItemRangeInserted(begin, items.size());
    }

    public void empty() {
      items.clear();
      notifyDataSetChanged();
    }

    @Override public int getItemCount() {
      return items.size();
    }
  }

  static class HackerNewsViewHolder extends RecyclerView.ViewHolder {
    @Bind(android.R.id.text1) TextView title;

    @OnClick(android.R.id.text1) void onItemClick(TextView title) {
      Item item = (Item) title.getTag();
      title.getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(item.url)));
    }

    @OnLongClick(android.R.id.text1) boolean onItemLongClick(TextView title) {
      Item item = (Item) title.getTag();
      final Context context = title.getContext();
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType(context.getString(R.string.mime_text_plain));
      intent.putExtra(Intent.EXTRA_TITLE, item.title);
      intent.putExtra(Intent.EXTRA_TEXT, String.format("[%s] %s from Hacker News", item.title, item.url));
      context.startActivity(intent);
      return true;
    }

    HackerNewsViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
