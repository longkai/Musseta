package yuejia.liu.musseta.components.home.product;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.components.home.HomeActivity;
import yuejia.liu.musseta.misc.ErrorMetaRetriever;
import yuejia.liu.musseta.misc.MathHelper;
import yuejia.liu.musseta.misc.NetworkWatcher;
import yuejia.liu.musseta.misc.RoundedTransformation;
import yuejia.liu.musseta.ui.ViewInstanceStateLifecycle;
import yuejia.liu.musseta.widgets.EnhancedRecyclerView;

/**
 * The Product Hunt listing layout.
 */
public class ProductHuntLayout extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener, ViewInstanceStateLifecycle {
  public static final int PRE_LOADING_OFFSET = 7;

  private final CompositeSubscription subscriptions = new CompositeSubscription();
  private final Object                picassoTag    = subscriptions;

  @Bind(android.R.id.empty)    ImageView            emptyView;
  @Bind(android.R.id.progress) ProgressBar          progressBar;
  @Bind(R.id.refresh_layout)   SwipeRefreshLayout   swipeRefreshLayout;
  @Bind(R.id.recycler_view)    EnhancedRecyclerView recyclerView;

  @BindInt(R.integer.product_hunt_grid_span)      int    gridSpan;
  @BindColor(R.color.product_hunt_accent)         int    productHuntAccentColor;
  @BindString(R.string.product_hunt_access_token) String accessTokenKey;

  @Inject Gson               gson;
  @Inject Picasso            picasso;
  @Inject SharedPreferences  preferences;
  @Inject NetworkWatcher     networkWatcher;
  @Inject ErrorMetaRetriever errorMetaRetriever;
  @Inject ProductHuntApi     productHuntApi;

  private PostsAdapter               postsAdapter;
  private StaggeredGridLayoutManager layoutManager;

  private int        days_ago;
  private boolean    loading;
  private SavedState savedState;

  public ProductHuntLayout(Context context) {
    this(context, null);
  }

  public ProductHuntLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    bootstrap(context, attrs, 0);
  }

  public ProductHuntLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    bootstrap(context, attrs, defStyle);
  }

  private void bootstrap(Context context, @Nullable AttributeSet attrs, int defStyle) {
    ((HomeActivity) context).getActivityComponent().inject(this);
    populateLayout(context);

    postsAdapter = new PostsAdapter(context, picasso, picassoTag);
    layoutManager = new StaggeredGridLayoutManager(gridSpan, StaggeredGridLayoutManager.VERTICAL);
    layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
  }

  private void populateLayout(Context context) {
    inflate(context, R.layout.merge_home_pager_layout, this);
    ButterKnife.bind(this);

    picasso.load(R.mipmap.empty_list_4x)
        .tag(picassoTag)
        .resize(getResources().getDisplayMetrics().widthPixels, 0)
        .into(emptyView);
    recyclerView.setEmptyView(emptyView);

    swipeRefreshLayout.setEnabled(false);
    swipeRefreshLayout.setColorSchemeColors(productHuntAccentColor);
    swipeRefreshLayout.setOnRefreshListener(this);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(postsAdapter);

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int[] positions = layoutManager.findLastVisibleItemPositions(null);
        int last = MathHelper.max(positions);
        if (last >= layoutManager.getItemCount() - PRE_LOADING_OFFSET) {
          requestPosts();
        }
      }
    });

    if (savedState != null) {
      days_ago = savedState.days_ago;
      postsAdapter.appendAll(savedState.posts);
      layoutManager.scrollToPosition(savedState.lastFirstPosition);

      afterRequest(null);
      return;
    }

    if (!networkWatcher.hasNetwork()) {
      afterRequest(RetrofitError.networkError(null, new IOException(getResources().getString(R.string.network_error))));
      return;
    }

    if (!preferences.contains(accessTokenKey)) {
      requestAccessToken();
    } else {
      requestPosts();
    }
  }

  private void requestPosts() {
    if (loading) {
      return;
    }

    loading = true;

    // if it' s the first loading or its retry
    if (postsAdapter.getItemCount() == 0) {
      progressBar.setVisibility(VISIBLE);
    }

    Subscription subscription = productHuntApi.posts(days_ago)
        .subscribeOn(Schedulers.io())
        .map((Func1<Response, List<Post>>) response -> {
          InputStream in = null;
          try {
            in = response.getBody().in();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new InputStreamReader(in));
            JsonArray array = jsonElement.getAsJsonObject().getAsJsonArray("posts");
            return gson.fromJson(array, new TypeToken<List<Post>>() {}.getType());
          } catch (IOException e) {
            throw new RuntimeException(e);
          } finally {
            if (in != null) {
              try {
                in.close();
              } catch (IOException ignored) {
              }
            }
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(posts -> {
          if (days_ago == 0) {
            postsAdapter.empty();
          }
          postsAdapter.appendAll(posts);
        }, throwable -> afterRequest(throwable), () -> {
          afterRequest(null);
          days_ago++;
        });
    subscriptions.add(subscription);
  }

  private void afterRequest(Throwable e) {
    loading = false;
    swipeRefreshLayout.setEnabled(true);
    swipeRefreshLayout.setRefreshing(false);
    progressBar.setVisibility(GONE);

    if (e != null) {
      Pair<String, Boolean> pair = errorMetaRetriever.retrieve(e);
      Snackbar.make(this, pair.first, Snackbar.LENGTH_INDEFINITE)
          .setAction(R.string.retry, v -> requestPosts())
          .show();
    }
  }

  private void requestAccessToken() {
    progressBar.setVisibility(VISIBLE);

    Subscription subscription = productHuntApi.token(new Token.RequestTokenBody())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(token -> {
          // TODO: 12/9/15 token expired, refresh etc.
          SharedPreferencesCompat.EditorCompat.getInstance()
              .apply(preferences.edit().putString(accessTokenKey, token.access_token));

          requestPosts();
        }, throwable -> {
          Snackbar.make(ProductHuntLayout.this, "Request Product Hunt fail.", Snackbar.LENGTH_INDEFINITE)
              .setAction(R.string.retry, v -> {
                requestAccessToken();
              }).show();
          progressBar.setVisibility(GONE);
        });
    subscriptions.add(subscription);
  }

  @Override protected void onDetachedFromWindow() {
    picasso.cancelTag(picassoTag);
    subscriptions.unsubscribe();
    recyclerView.clearOnScrollListeners();
    super.onDetachedFromWindow();
  }

  @Override public Parcelable onSaveInstanceState() {
    if (layoutManager.getItemCount() == 0) {
      return null;
    }
    Parcelable parcelable = super.onSaveInstanceState();
    SavedState savedState = new SavedState(parcelable);
    savedState.days_ago = days_ago;
    int[] positions = layoutManager.findFirstVisibleItemPositions(null);
    savedState.lastFirstPosition = MathHelper.min(positions);
    savedState.posts = postsAdapter.posts;
    return savedState;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    savedState = (SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());
  }

  @Override public void onRefresh() {
    if (loading) {
      swipeRefreshLayout.setRefreshing(false);
      return;
    }

    days_ago = 0;
    requestPosts();
  }

  private static class SavedState extends BaseSavedState {
    private int        days_ago;
    private int        lastFirstPosition;
    private List<Post> posts;

    public SavedState(Parcel source) {
      super(source);
      days_ago = source.readInt();
      lastFirstPosition = source.readInt();
      source.readArray(null);
    }

    public SavedState(Parcelable superState) {
      super(superState);
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(days_ago);
      out.writeInt(lastFirstPosition);
      out.writeList(posts);
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

  private static class PostsAdapter extends RecyclerView.Adapter<PostCardViewHolder> {
    private final int avatarRoundRadius;

    private final List<Post>      posts;
    private final Picasso         picasso;
    private final Object          picassoTag;
    private final LoadingDrawable loadingPlaceHolder;

    public PostsAdapter(Context context, Picasso picasso, Object picassoTag) {
      Resources resources = context.getResources();
      avatarRoundRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
          resources.getDimensionPixelSize(R.dimen.product_hunt_card_avatar_bound), resources.getDisplayMetrics());

      posts = new ArrayList<>();
      this.picasso = picasso;
      this.picassoTag = picassoTag;
      loadingPlaceHolder = new LoadingDrawable.Builder().displayMetrics(resources.getDisplayMetrics()).build();
    }

    @Override public PostCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new PostCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.widge_product_hunt_card, parent, false));
    }

    @Override public void onBindViewHolder(PostCardViewHolder holder, int position) {
      Post post = posts.get(position);
      holder.itemView.setTag(post);

      int width = holder.itemView.getResources().getDisplayMetrics().widthPixels / 2;
      picasso.load(post.screenshot_url._300px)
          .placeholder(loadingPlaceHolder)
          .tag(picassoTag)
          .resize(width, 0)
          .into(holder.image);
      holder.title.setText(post.name);
      holder.text1.setText(post.tagline);
      holder.points.setText(String.valueOf(post.votes_count));

      picasso.load(post.user.image_url.original)
          .transform(new RoundedTransformation(avatarRoundRadius))
          .tag(picassoTag)
          .fit()
          .into(holder.avatar);
    }

    @Override public int getItemCount() {
      return posts.size();
    }

    public void appendAll(List<Post> posts) {
      int begin = this.posts.size();
      this.posts.addAll(posts);
      notifyItemRangeInserted(begin, posts.size());
    }

    public void empty() {
      int size = posts.size();
      posts.clear();
      notifyItemRangeRemoved(0, size);
    }
  }

  static class PostCardViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.image)         ImageView image;
    @Bind(android.R.id.title) TextView  title;
    @Bind(android.R.id.text1) TextView  text1;
    @Bind(R.id.avatar)        ImageView avatar;
    @Bind(R.id.points)        TextView  points;

    @OnClick(R.id.root_view) void click(View view) {
      Post post = (Post) view.getTag();
      final HomeActivity activity = (HomeActivity) view.getContext();
      activity.getTracker().send(new HitBuilders.EventBuilder()
          .setCategory(activity.getString(R.string.product_hunt))
          .setAction("View")
          .setLabel("Item")
          .setValue(1)
          .build());
      view.getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(post.discussion_url)));
    }

    public PostCardViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
