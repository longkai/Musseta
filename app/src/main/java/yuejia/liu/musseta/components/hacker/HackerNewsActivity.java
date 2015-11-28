package yuejia.liu.musseta.components.hacker;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import retrofit.RetrofitError;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.settings.SettingsActivity;
import yuejia.liu.musseta.ui.ItemTouchHelperAdapter;
import yuejia.liu.musseta.ui.MussetaActivity;
import yuejia.liu.musseta.ui.ResourceManager;
import yuejia.liu.musseta.widgets.ListDividerItemDecorator;


/**
 * Hacker News ui.
 */
public class HackerNewsActivity extends MussetaActivity<HackerNewsComponent> implements SwipeRefreshLayout.OnRefreshListener {
  private static final int ACTION_SHARE = 0x8888;

  private static final String KEY_PREVIOUS_POSITION = "key_previous_position";
  private static final String KEY_HACKER_NEWS_ITEMS = "key_hacker_news_items";

  @HackerNews
  @Inject CompositeSubscription subscriptions;
  @Inject HackerNewsPresenter   presenter;
  @Inject ResourceManager       resourceManager;
  @Inject Tracker               tracker;

  @Bind(R.id.toolbar)          Toolbar            toolbar;
  @Bind(android.R.id.progress) ProgressBar        progress;
  @Bind(R.id.recycler_view)    RecyclerView       recyclerView;
  @Bind(R.id.refresh_layout)   SwipeRefreshLayout refreshLayout;

  HackerNewsAdapter        hackerNewsAdapter;
  LinearLayoutManager      layoutManager;
  ListDividerItemDecorator dividerItemDecorator;
  ItemTouchHelper          itemTouchHelper;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.layout_hacker_news);
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

  @Override protected HackerNewsComponent setupActivityComponent() {
    return Musseta.get(this).getMussetaComponent().plus(new HackerNewsModule(this));
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // if we use layout manager, at some point, the layout manager may not attach to the adapter
    if (hackerNewsAdapter.items.size() > 0) {
      outState.putParcelableArrayList(KEY_HACKER_NEWS_ITEMS, hackerNewsAdapter.items);
      outState.putInt(KEY_PREVIOUS_POSITION, layoutManager.findFirstVisibleItemPosition());
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case ACTION_SHARE:
        // note: the result code seems always cancel, in this case, we assume the user opening another activity gain one share action
        tracker.send(new HitBuilders.EventBuilder()
            .setCategory(getString(R.string.hacker_news))
            .setAction("Share")
            .setLabel("Item")
            .setValue(1)
            .build());
        break;
    }
  }

  @Override protected void onDestroy() {
    recyclerView.removeItemDecoration(dividerItemDecorator); // let it destroy callbacks
    itemTouchHelper.attachToRecyclerView(null);
    subscriptions.unsubscribe();
    super.onDestroy();
  }

  @Override public void onRefresh() {
    hackerNewsAdapter.empty();
    presenter.refresh();
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

    itemTouchHelper = setupItemTouchHelper();
    itemTouchHelper.attachToRecyclerView(recyclerView);

    hackerNewsAdapter = new HackerNewsAdapter();
    recyclerView.setAdapter(hackerNewsAdapter);
  }

  private ItemTouchHelper setupItemTouchHelper() {
    return new ItemTouchHelper(new ItemTouchHelper.Callback() {
      @Override public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0, swipeFlags);
      }

      @Override
      public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
      }

      @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        hackerNewsAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        recyclerView.removeView(viewHolder.itemView); // redraw the divider line
      }

      @Override public boolean isItemViewSwipeEnabled() {
        return true;
      }

      @Override
      public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        ViewCompat.setAlpha(viewHolder.itemView, 1 - Math.abs(dX) * 2 / viewHolder.itemView.getWidth());
      }
    });
  }

  private void setupToolbar() {
    toolbar.setTitle(R.string.hacker_news);
    setSupportActionBar(toolbar);
    final GestureDetectorCompat detectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
      @Override public boolean onDoubleTap(MotionEvent e) {
        recyclerView.smoothScrollToPosition(0);
        return true;
      }
    });
    toolbar.setOnTouchListener((v, event) -> detectorCompat.onTouchEvent(event));
  }

  // presenter method
  void showRefreshing(boolean refreshing) {
    if (refreshLayout.isRefreshing() != refreshing) {
      refreshLayout.setRefreshing(refreshing);
    }
  }

  void onNetworkError(RetrofitError error) {
    Snackbar.make(recyclerView, error == null ? getString(R.string.network_problem) : error.getMessage(), Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.retry, v -> {
          presenter.present();
        })
        .show();
  }

  void showLoading(boolean loading) {
    progress.setVisibility(loading ? View.VISIBLE : View.GONE);
  }

  void appendAll(ArrayList<Item> items) {
    hackerNewsAdapter.appendAll(items);
  }

  void appendItem(Item item) {
    hackerNewsAdapter.append(item);
  }
  // presenter

  static class HackerNewsAdapter extends RecyclerView.Adapter<HackerNewsViewHolder> implements ItemTouchHelperAdapter {
    private final ArrayList<Item> items = new ArrayList<>();

    @Override public HackerNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final Context context = parent.getContext();
      View itemView = LayoutInflater.from(context).inflate(R.layout.widget_hacker_news_item, parent, false);
      return new HackerNewsViewHolder(itemView);
    }

    @Override public void onBindViewHolder(HackerNewsViewHolder holder, int position) {
      Item item = items.get(position);
      holder.itemView.setTag(item);
      final Context context = holder.itemView.getContext();

      SpannableString title = new SpannableString(String.format("%s (%s)", item.title, Uri.parse(item.url).getHost()));
      int start = item.title.length() + 1;
      int end = title.length();
      title.setSpan(new RelativeSizeSpan(.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.material_grey_500)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      holder.title.setText(title);

      holder.subTitle.setText(context.getString(
          R.string.hacker_news_item_subtitle, item.score, item.by, DateUtils.getRelativeTimeSpanString(item.time)));

      holder.countingView.setCounting(item.descendants);
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

    @Override public void onItemMove(int fromPosition, int toPosition) {
      if (fromPosition < toPosition) {
        for (int i = fromPosition; i < toPosition; i++) {
          Collections.swap(items, i, i + 1);
        }
      } else {
        for (int i = fromPosition; i > toPosition; i--) {
          Collections.swap(items, i, i - 1);
        }
      }
      notifyItemMoved(fromPosition, toPosition);
    }

    @Override public void onItemDismiss(int position) {
      items.remove(position);
      notifyItemRemoved(position);
    }
  }

  static class HackerNewsViewHolder extends RecyclerView.ViewHolder {
    @Bind(android.R.id.title)  TextView          title;
    @Bind(android.R.id.text1)  TextView          subTitle;
    @Bind(R.id.reply_counting) ReplyCountingView countingView;

    @OnClick({R.id.root_view, R.id.reply_counting}) void onItemClick(View view) {
      Item item = (Item) itemView.getTag();
      HackerNewsActivity activity = (HackerNewsActivity) view.getContext();
      switch (view.getId()) {
        case R.id.root_view:
          activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(item.url)));

          activity.tracker.send(new HitBuilders.EventBuilder()
              .setCategory(activity.getString(R.string.hacker_news))
              .setAction("View")
              .setLabel("Item")
              .setValue(1)
              .build());
          break;
        case R.id.reply_counting:
          activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://news.ycombinator.com/item?id=" + item.id)));

          activity.tracker.send(new HitBuilders.EventBuilder()
              .setCategory(activity.getString(R.string.hacker_news))
              .setAction("View")
              .setLabel("Comments")
              .setValue(1)
              .build());
          break;
      }
    }

    @OnLongClick(R.id.root_view) boolean onItemLongClick(View view) {
      Item item = (Item) view.getTag();
      final HackerNewsActivity activity = (HackerNewsActivity) view.getContext();
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType(activity.getString(R.string.mime_text_plain));
      intent.putExtra(Intent.EXTRA_TITLE, item.title);
      intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.hacker_news_share_content, item.title, item.url));
      activity.startActivityForResult(intent, ACTION_SHARE);
      return true;
    }

    HackerNewsViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
