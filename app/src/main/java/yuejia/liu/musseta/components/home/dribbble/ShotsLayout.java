package yuejia.liu.musseta.components.home.dribbble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.analytics.HitBuilders;
import com.squareup.picasso.Picasso;
import retrofit.RetrofitError;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.home.HomeActivity;
import yuejia.liu.musseta.components.web.WebActivity;
import yuejia.liu.musseta.misc.ErrorMetaRetriever;
import yuejia.liu.musseta.misc.NetworkWatcher;
import yuejia.liu.musseta.misc.RoundedTransformation;
import yuejia.liu.musseta.ui.ViewInstanceStateLifecycle;
import yuejia.liu.musseta.widgets.EnhancedRecyclerView;

/**
 * Dribble shots layout.
 */
public class ShotsLayout extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener, ViewInstanceStateLifecycle {
  public static final int PRE_LOADING_OFFSET = 7;

  private final CompositeSubscription subscriptions = new CompositeSubscription();
  private final Object                picassoTag    = subscriptions;

  @Bind(android.R.id.empty)    ImageView            emptyView;
  @Bind(android.R.id.progress) ProgressBar          progressBar;
  @Bind(R.id.refresh_layout)   SwipeRefreshLayout   swipeRefreshLayout;
  @Bind(R.id.recycler_view)    EnhancedRecyclerView recyclerView;

  @BindInt(R.integer.dribble_shot_grid_span) int gridSpan;

  @Inject Picasso            picasso;
  @Inject DribbbleApi        dribbbleApi;
  @Inject NetworkWatcher     networkWatcher;
  @Inject ErrorMetaRetriever errorMetaRetriever;

  private GridLayoutManager layoutManager;
  private ShotsAdapter      shotsAdapter;
  private ShotDecoration    shotDecoration;

  private int page = 1; // api first page starts from 1
  private boolean    loading;
  private SavedState savedState;

  public ShotsLayout(Context context) {
    this(context, null);
  }

  public ShotsLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    bootstrap(context, attrs, 0, 0);
  }

  public ShotsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    bootstrap(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ShotsLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    bootstrap(context, attrs, defStyleAttr, defStyleRes);
  }

  private void bootstrap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    ((HomeActivity) context).getActivityComponent().inject(this);
    populateLayout(context);

    layoutManager = new GridLayoutManager(context, gridSpan);
    shotsAdapter = new ShotsAdapter(context, picasso, picassoTag);
    shotDecoration = new ShotDecoration(context, gridSpan);
  }

  private void populateLayout(Context context) {
    inflate(context, R.layout.merge_home_pager_layout, this);
    ButterKnife.bind(this);
    swipeRefreshLayout.setColorSchemeColors(R.color.dribbble_accent);
    swipeRefreshLayout.setEnabled(false);
    swipeRefreshLayout.setOnRefreshListener(this);
    picasso.load(R.mipmap.empty_grid_4x)
        .tag(picassoTag)
        .resize(getResources().getDisplayMetrics().widthPixels, 0)
        .into(emptyView);
    recyclerView.setEmptyView(emptyView);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(shotsAdapter);
    recyclerView.addItemDecoration(shotDecoration);

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (layoutManager.findLastVisibleItemPosition() >= layoutManager.getItemCount() - PRE_LOADING_OFFSET) {
          requestShots();
        }
      }
    });

    if (savedState != null) {
      page = savedState.page;
      shotsAdapter.addAll(savedState.shots);
      layoutManager.scrollToPosition(savedState.lastFirstPosition);

      afterRequest(null);
    } else if (networkWatcher.hasNetwork()) {
      requestShots();
    } else {
      afterRequest(RetrofitError.networkError(null, new IOException(getResources().getString(R.string.network_error))));
    }
  }

  private void requestShots() {
    if (loading) {
      return;
    }

    loading = true;

    // if the first auto time loading
    if (!swipeRefreshLayout.isRefreshing() && layoutManager.getItemCount() == 0) {
      progressBar.setVisibility(VISIBLE);
    }

    Subscription subscription = dribbbleApi.shots(page)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(shots -> {
          if (swipeRefreshLayout.isRefreshing()) {
            shotsAdapter.clear();
          }
          shotsAdapter.addAll(shots);
        }, throwable -> afterRequest(throwable), () -> {
          page++;
          afterRequest(null);
        });
    subscriptions.add(subscription);
  }

  protected void afterRequest(Throwable e) {
    progressBar.setVisibility(GONE);
    swipeRefreshLayout.setRefreshing(false);
    swipeRefreshLayout.setEnabled(true);

    loading = false;

    if (e != null) {
      Pair<String, Boolean> retrieve = errorMetaRetriever.retrieve(e);
      Snackbar.make(this, retrieve.first, Snackbar.LENGTH_INDEFINITE)
          .setAction(R.string.retry, v -> requestShots())
          .show();
    }
  }

  @Override public void onRefresh() {
    if (loading) {
      swipeRefreshLayout.setRefreshing(false);
      return;
    }

    page = 1;
    requestShots();
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    subscriptions.unsubscribe();
    picasso.cancelTag(picassoTag);
    recyclerView.removeItemDecoration(shotDecoration);
    recyclerView.clearOnScrollListeners();
  }

  @Override public Parcelable onSaveInstanceState() {
    if (layoutManager.getItemCount() == 0) {
      return null;
    }
    Parcelable parcelable = super.onSaveInstanceState();
    SavedState savedState = new SavedState(parcelable);
    List<Shot> shots = shotsAdapter.shots;

    savedState.shots = shots;
    savedState.page = page;
    savedState.lastFirstPosition = layoutManager.findFirstVisibleItemPosition();
    return savedState;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    savedState = (SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());
  }

  private static class SavedState extends BaseSavedState {
    private List<Shot> shots;
    private int        page;
    private int        lastFirstPosition;

    public SavedState(Parcel source) {
      super(source);
      shots = source.readArrayList(Shot.class.getClassLoader());
      page = source.readInt();
      lastFirstPosition = source.readInt();
    }

    public SavedState(Parcelable superState) {
      super(superState);
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeList(shots);
      out.writeInt(page);
      out.writeInt(lastFirstPosition);
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

  private static class ShotsAdapter extends RecyclerView.Adapter<ShotViewHolder> {
    private final List<Shot> shots;
    private final Picasso    picasso;
    private final Object     picassoTag;

    private final Drawable placeHolder;

    public ShotsAdapter(Context context, Picasso picasso, Object picassoTag) {
      this.picasso = picasso;
      this.picassoTag = picassoTag;

      shots = new ArrayList<>();

      placeHolder = new LoadingDrawable(context);
    }

    @Override public ShotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ShotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_dribbble_shot_card, parent, false));
    }

    @Override public void onBindViewHolder(ShotViewHolder holder, int position) {
      Shot shot = shots.get(position);
      holder.itemView.setTag(shot);

      float ratio = shot.width * 1f / shot.height;
      int width = holder.itemView.getResources().getDisplayMetrics().widthPixels;
      int height = (int) (width / ratio);
      placeHolder.setBounds(holder.itemView.getLeft(), holder.itemView.getTop(), holder.itemView.getLeft() + width, holder.itemView.getTop() + height);

      picasso.load(shot.images.normal)
          .tag(picassoTag)
          .placeholder(placeHolder)
          .resize(width, height)
          .into(holder.shot);

      picasso.load(shot.user.avatar_url)
          .tag(picassoTag)
          .transform(new RoundedTransformation(holder.itemView.getResources().getDimensionPixelSize(R.dimen.dribbble_player_avatar_bound)))
          .fit()
          .into(holder.avatar);

      holder.title.setText(shot.user.name);

      holder.viewCount.setText(String.valueOf(shot.views_count));
      holder.commentsCount.setText(String.valueOf(shot.comments_count));
      holder.likeCount.setText(String.valueOf(shot.likes_count));
    }

    @Override public int getItemCount() {
      return shots.size();
    }

    public void addAll(List<Shot> shots) {
      int before = this.shots.size();
      this.shots.addAll(shots);
      notifyItemRangeInserted(before, shots.size());
    }

    public void clear() {
      int itemCount = getItemCount();
      shots.clear();
      notifyItemRangeRemoved(0, itemCount);
    }
  }


  static class ShotViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.image)          ImageView shot;
    @Bind(R.id.avatar)         ImageView avatar;
    @Bind(android.R.id.title)  TextView  title;
    @Bind(R.id.view_count)     TextView  viewCount;
    @Bind(R.id.comments_count) TextView  commentsCount;
    @Bind(R.id.like_count)     TextView  likeCount;

    @OnClick(R.id.root_view) void click(View view) {
      final HomeActivity activity = (HomeActivity) view.getContext();
      activity.getTracker().send(new HitBuilders.EventBuilder()
          .setCategory(activity.getString(R.string.dribbble))
          .setAction("View")
          .setLabel("Item")
          .setValue(1)
          .build());
      Shot shot = (Shot) view.getTag();
      WebActivity.startActivity(activity, new DribbleWebViewDelegate(shot));
    }

    public ShotViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  private static class ShotDecoration extends RecyclerView.ItemDecoration {
    private final int span;
    private final int offset;

    public ShotDecoration(Context context, int span) {
      this.span = span;
      offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
    }

    @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      super.getItemOffsets(outRect, view, parent, state);
      int position = parent.getChildAdapterPosition(view);
      int i = position % span;
      int bottomOffset = parent.getLayoutManager().getItemCount() - position <= span ? offset : 0;
      int halfOffset = span == 1 ? offset : offset >> 1; // in case single column grid

      if (i == 0) {
        // left border
        outRect.set(offset, offset, halfOffset, bottomOffset);
      } else if (i == (span - 1)) {
        // right border
        outRect.set(halfOffset, offset, offset, bottomOffset);
      } else {
        // center
        outRect.set(halfOffset, offset, halfOffset, bottomOffset);
      }
    }
  }
}
