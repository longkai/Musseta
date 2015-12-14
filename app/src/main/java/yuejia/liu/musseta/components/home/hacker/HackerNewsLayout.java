package yuejia.liu.musseta.components.home.hacker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.analytics.HitBuilders;
import com.squareup.picasso.Picasso;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.home.HomeActivity;
import yuejia.liu.musseta.misc.ErrorMetaRetriever;
import yuejia.liu.musseta.misc.NetworkWatcher;
import yuejia.liu.musseta.ui.ViewInstanceStateLifecycle;
import yuejia.liu.musseta.widgets.EnhancedRecyclerView;

/**
 * The Hacker News layout.
 */
public class HackerNewsLayout extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener, ViewInstanceStateLifecycle {
  public static final int PRE_LOADING_OFFSET = 7;
  public static final int PER_PAGE           = 20;

  @Bind(android.R.id.empty)    ImageView            emptyView;
  @Bind(android.R.id.progress) ProgressBar          progressBar;
  @Bind(R.id.refresh_layout)   SwipeRefreshLayout   swipeRefreshLayout;
  @Bind(R.id.recycler_view)    EnhancedRecyclerView recyclerView;

  @BindColor(R.color.hacker_news_accent) int refreshColor;

  @Inject Picasso            picasso;
  @Inject HackerNewsApi      hackerNewsApi;
  @Inject NetworkWatcher     networkWatcher;
  @Inject ErrorMetaRetriever errorMetaRetriever;

  private final CompositeSubscription subscriptions = new CompositeSubscription();
  private final Object                picassoTag    = subscriptions;

  private final ArrayList<Long> topStories = new ArrayList<>();

  private HackerNewsAdapter   hackerNewsAdapter;
  private LinearLayoutManager linearLayoutManager;
  private HackerNewsDivider   listDividerItemDecorator;

  private int     count;
  private boolean loading;

  private SavedState savedState;

  public HackerNewsLayout(Context context) {
    this(context, null);
  }

  public HackerNewsLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    bootstrap(context, attrs, 0, 0);
  }

  public HackerNewsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    bootstrap(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public HackerNewsLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    bootstrap(context, attrs, defStyleAttr, defStyleRes);
  }

  private void bootstrap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    ((HomeActivity) context).getActivityComponent().inject(this);
    populateLayout(context);

    linearLayoutManager = new LinearLayoutManager(context);
    hackerNewsAdapter = new HackerNewsAdapter();
    listDividerItemDecorator = new HackerNewsDivider(context);
  }

  private void populateLayout(Context context) {
    inflate(context, R.layout.merge_home_pager_layout, this);
    ButterKnife.bind(this);

    picasso.load(R.mipmap.empty_list_4x)
        .tag(picassoTag)
        .resize(getResources().getDisplayMetrics().widthPixels, 0)
        .into(emptyView);
    recyclerView.setEmptyView(emptyView);

    swipeRefreshLayout.setColorSchemeColors(refreshColor);
    swipeRefreshLayout.setOnRefreshListener(this);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    recyclerView.setHasFixedSize(true);
    recyclerView.addItemDecoration(listDividerItemDecorator);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(hackerNewsAdapter);
    recyclerView.setItemAnimator(new DefaultItemAnimator());

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Timber.d("count %d, last %d", count, linearLayoutManager.findLastVisibleItemPosition());
        if (linearLayoutManager.findLastVisibleItemPosition() >= hackerNewsAdapter.getItemCount() - PRE_LOADING_OFFSET) {
          performRequest();
        }
      }
    });

    if (savedState != null) {
      topStories.addAll(savedState.topStories);
      hackerNewsAdapter.appendAll(savedState.items);
      linearLayoutManager.scrollToPosition(savedState.lastPosition);
      count = savedState.count;
      afterRequest(null);
      return;
    }

    if (networkWatcher.hasNetwork()) {
      bootstrap();
    } else {
      afterRequest(RetrofitError.networkError(null, new IOException(getResources().getString(R.string.network_error))));
    }
  }

  private void bootstrap() {
    // if not triggered by user
    if (!swipeRefreshLayout.isRefreshing()) {
      progressBar.setVisibility(VISIBLE);
    }
    Subscription subscription = hackerNewsApi.topStories()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(longs -> {
          count = 0;
          topStories.clear();
          topStories.addAll(Arrays.asList(longs));
          hackerNewsAdapter.clear();
          performRequest();
        }, throwable -> {
          afterRequest(throwable);
        });
    subscriptions.add(subscription);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    subscriptions.unsubscribe();
    picasso.cancelTag(picassoTag);
    recyclerView.removeItemDecoration(listDividerItemDecorator);
  }

  private void performRequest() {
    final int total = topStories.size();
    if (count >= total || loading) {
      return;
    }

    loading = true;
    swipeRefreshLayout.setEnabled(false);

    Subscription subscription = Observable.create(
        new Observable.OnSubscribe<Item>() {
          @Override public void call(Subscriber<? super Item> subscriber) {
            for (int i = count, j = count + PER_PAGE; i < j && i < total; i++) {
              Long id = topStories.get(i);
              Item item = hackerNewsApi.item(id);
              subscriber.onNext(item);
              count++;
            }
            subscriber.onCompleted();
          }
        })
        .onBackpressureBuffer()
        .filter(item -> !TextUtils.isEmpty(item.url) && !item.deleted && !item.dead)
        .doOnNext(item -> item.time = item.time * 1000)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(item -> hackerNewsAdapter.append(item), throwable -> afterRequest(throwable), () -> afterRequest(null));

    subscriptions.add(subscription);
  }

  private void afterRequest(Throwable e) {
    loading = false;
    progressBar.setVisibility(GONE);
    swipeRefreshLayout.setEnabled(true);
    swipeRefreshLayout.setRefreshing(false);

    onError(e);
  }

  // TODO: 12/7/15 makes it a specific error handler
  private void onError(Throwable e) {
    if (e != null) {
      Pair<String, Boolean> pair = errorMetaRetriever.retrieve(e);
      if (pair.second) {
        Snackbar.make(recyclerView, pair.first, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry, v -> {
              if (topStories.size() == 0) {
                bootstrap();
              } else {
                performRequest();
              }
            })
            .show();
      } else {
        Snackbar.make(recyclerView, pair.first, Snackbar.LENGTH_INDEFINITE).show();
      }
    }
  }

  @Override public void onRefresh() {
    if (loading) {
      swipeRefreshLayout.setRefreshing(false);
      return;
    }

    bootstrap();
  }

  @Override public Parcelable onSaveInstanceState() {
    // if not fulfill one page requests, stop save instance
    if (linearLayoutManager.getItemCount() < PER_PAGE) {
      return null;
    }

    Parcelable parcelable = super.onSaveInstanceState();
    SavedState savedState = new SavedState(parcelable);
    savedState.lastPosition = linearLayoutManager.findFirstVisibleItemPosition();
    savedState.count = count;
    savedState.topStories = topStories;
    savedState.items = hackerNewsAdapter.items;
    return savedState;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    savedState = (SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());
  }

  private static class SavedState extends BaseSavedState implements Parcelable {
    private int             lastPosition;
    private int             count;
    private ArrayList<Long> topStories;
    private ArrayList<Item> items;

    public SavedState(Parcel source) {
      super(source);
      lastPosition = source.readInt();
      count = source.readInt();
      topStories = source.readArrayList(null);
      items = source.readArrayList(null);
    }

    public SavedState(Parcelable superState) {
      super(superState);
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(lastPosition);
      out.writeInt(count);
      out.writeList(topStories);
      out.writeList(items);
    }

    public static final Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
      @Override public SavedState createFromParcel(Parcel in, ClassLoader loader) {
        return new SavedState(in);
      }

      @Override public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    });
  }

  private static class HackerNewsAdapter extends RecyclerView.Adapter<HackerNewsViewHolder> {
    private final ArrayList<Item> items = new ArrayList<>();

    @Override public HackerNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new HackerNewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_hacker_news_item, parent, false));
    }

    @Override public void onBindViewHolder(HackerNewsViewHolder holder, int position) {
      Item item = items.get(position);
      holder.itemView.setTag(item);
      holder.replayCounting.setTag(item);

      holder.title.setText(item.title);
      holder.meta.setText(String.format("%s · %s", Uri.parse(item.url).getHost(), DateUtils.getRelativeTimeSpanString(item.time)));
      holder.points.setText(String.valueOf(item.score));
      holder.replayCounting.setText(String.valueOf(item.descendants));
    }

    @Override public int getItemCount() {
      return items.size();
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

    public void clear() {
      int size = items.size();
      if (size > 0) {
        items.clear();
        notifyItemRangeRemoved(0, size);
      }
    }
  }

  static class HackerNewsViewHolder extends RecyclerView.ViewHolder {
    @Bind(android.R.id.title)  TextView title;
    @Bind(android.R.id.text1)  TextView meta;
    @Bind(R.id.points)         TextView points;
    @Bind(R.id.reply_counting) TextView replayCounting;

    @OnClick({R.id.root_view, R.id.reply_counting}) void onItemClick(View view) {
      Item item = (Item) itemView.getTag();
      HomeActivity activity = (HomeActivity) view.getContext();
      switch (view.getId()) {
        case R.id.root_view:
          activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(item.url)));

          activity.getTracker().send(new HitBuilders.EventBuilder()
              .setCategory(activity.getString(R.string.hacker_news))
              .setAction("View")
              .setLabel("Item")
              .setValue(1)
              .build());
          break;
        case R.id.reply_counting:
          activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://news.ycombinator.com/item?id=" + item.id)));

          activity.getTracker().send(new HitBuilders.EventBuilder()
              .setCategory(activity.getString(R.string.hacker_news))
              .setAction("View")
              .setLabel("Comments")
              .setValue(1)
              .build());
          break;
      }
    }

    public HackerNewsViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  private static class HackerNewsDivider extends RecyclerView.ItemDecoration {
    private Drawable divider;

    public HackerNewsDivider(Context context) {
      TypedArray typedArray = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
      divider = typedArray.getDrawable(0);
      typedArray.recycle();

      divider = DrawableCompat.wrap(divider);
      DrawableCompat.setTintMode(divider, PorterDuff.Mode.SRC_OVER);
      DrawableCompat.setTint(divider, ContextCompat.getColor(context, R.color.hacker_news_divider_color));
    }

    @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
      super.onDrawOver(c, parent, state);
      final int size = parent.getChildCount();
      for (int i = 0; i < size; i++) {
        View child = parent.getChildAt(i);
        // simple one
        divider.setBounds(child.getPaddingLeft(), child.getBottom(), child.getRight() - child.getPaddingRight(), child.getBottom() + divider.getIntrinsicHeight());
        divider.draw(c);
      }
    }
  }
}
