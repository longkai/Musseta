package yuejia.liu.musseta.components.hacker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import yuejia.liu.musseta.Musseta;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.ui.MussetaRecyclerFragment;

/**
 * Staggered grid item.  NOTE: this is still in test environment!
 */
public class HackerNewsGridFragment extends MussetaRecyclerFragment implements SwipeRefreshLayout.OnRefreshListener {
  private static final int DEFAULT_VERTICAL_GRID_SPAN = 2;

  private static final String KEY_SINGLE_COLUMN = "single_column";

  @Inject HackerNewsApi hackerNewsApi;

  private Subscription listRequest    = Subscriptions.empty();
  private Subscription colorProcessor = Subscriptions.empty();

  private HackerNewsAdapter          adapter;
  private StaggeredGridLayoutManager layoutManager;

  private static Pattern pattern = Pattern.compile("^material_.+_400$");

  private static List<Integer> colors = new ArrayList<>();

  private int color500;
  private int color700;
  private int color900;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getSwipeRefreshLayout().setOnRefreshListener(this);

    getRecyclerView().setVerticalScrollBarEnabled(true);
    getRecyclerView().addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int dimen = (int) (view.getResources().getDisplayMetrics().density * 4 + .5f);
        outRect.set(dimen, dimen, dimen, dimen);
      }
    });
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    processColor();

    int columnCount = savedInstanceState == null ? DEFAULT_VERTICAL_GRID_SPAN
        : savedInstanceState.getBoolean(KEY_SINGLE_COLUMN) ? 1 : DEFAULT_VERTICAL_GRID_SPAN;
    layoutManager = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
    getRecyclerView().setLayoutManager(layoutManager);
    adapter = new HackerNewsAdapter(getActivity(), getActivity().getLayoutInflater());
    getRecyclerView().setAdapter(adapter);

    performRequest();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.add(Menu.NONE, android.R.id.button1, Menu.NONE, "");
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    MenuItem span = menu.findItem(android.R.id.button1);
    span.setTitle(layoutManager.getSpanCount() == 1 ? R.string.multi_column_view : R.string.single_column_view);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.button1:
        layoutManager.setSpanCount(layoutManager.getSpanCount() == 1 ? DEFAULT_VERTICAL_GRID_SPAN : 1);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onRefresh() {
    performRequest();
  }

  private void changeRefreshColors() {
    getSwipeRefreshLayout().setColorSchemeColors(
        colors.get((int) (Math.random() * colors.size())),
        colors.get((int) (Math.random() * colors.size())),
        colors.get((int) (Math.random() * colors.size())),
        colors.get((int) (Math.random() * colors.size()))
    );
  }

  private void performRequest() {
    listRequest = Observable.create(new Observable.OnSubscribe<Item>() {
      @Override public void call(Subscriber<? super Item> subscriber) {
        try {
          long[] ids = hackerNewsApi.topStories();
          for (int i = 0; i < ids.length; i++) {
            subscriber.onNext(hackerNewsApi.item(ids[i]));
          }
        } catch (Exception e) {
          subscriber.onError(e);
        } finally {
          subscriber.onCompleted();
        }
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Item>() {
          @Override public void onCompleted() {
            getSwipeRefreshLayout().setRefreshing(false);
            changeRefreshColors();
          }

          @Override public void onError(Throwable e) {
            if (e instanceof RetrofitError) {
              RetrofitError retrofitError = (RetrofitError) e;
              Toast.makeText(getActivity(), retrofitError.getResponse().getReason(), Toast.LENGTH_SHORT).show();
            }
          }

          @Override public void onNext(Item item) {
            adapter.add(item);
            setRecyclerShown(true);
          }

          @Override public void onStart() {
            super.onStart();
            getSwipeRefreshLayout().setRefreshing(true);
            adapter.clear();
          }
        });
  }

  private void processColor() {
    colorProcessor = Observable.create(new Observable.OnSubscribe<Void>() {
      @Override public void call(Subscriber<? super Void> subscriber) {
        int target = (int) (Math.random() * 19); // status bar color
        colors.clear();
        Class<material.palette.R.color> palette = material.palette.R.color.class;
        Field[] fields = palette.getFields();
        for (Field field : fields) {
          String name = field.getName();
          Matcher matcher = pattern.matcher(name);
          if (matcher.matches()) {
            try {
              colors.add(getResources().getColor(field.getInt(null)));
              if (colors.size() == target) {
                String color = name.substring(0, name.length() - 3);
                color500 = getResources().getColor(palette.getField(color + "500").getInt(null));
                color700 = getResources().getColor(palette.getField(color + "700").getInt(null));
                color900 = getResources().getColor(palette.getField(color + "900").getInt(null));
              }
            } catch (Exception ignore) {
            }
          }
        }
        subscriber.onNext(null);
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Void>() {
          @Override public void call(Void aVoid) {
            changeRefreshColors();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              getActivity().getWindow().setStatusBarColor(color700);
              getActivity().getWindow().setNavigationBarColor(color900);
              ((HackerNewsActivity) getActivity()).setToolbarColor(color500);
            }
          }
        });
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(KEY_SINGLE_COLUMN, layoutManager.getSpanCount() == 1);
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroyView() {
    colorProcessor.unsubscribe();
    listRequest.unsubscribe();
    super.onDestroyView();
  }

  @Override protected void setupFragmentComponent() {
    Musseta.get(getActivity()).getMussetaComponent().plus(new HackerNewsModule()).inject(this);
  }

  static class HackerNewsViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    CardView cardView;

    public HackerNewsViewHolder(View itemView) {
      super(itemView);
      textView = (TextView) itemView.findViewById(android.R.id.title);
      cardView = (CardView) itemView;
    }
  }

  static class HackerNewsAdapter extends RecyclerView.Adapter<HackerNewsViewHolder> implements View.OnClickListener,
      View.OnLongClickListener {
    private Context        context;
    private List<Item>     items;
    private LayoutInflater layoutInflater;

    HackerNewsAdapter(Context context, List<Item> items, LayoutInflater layoutInflater) {
      this.context = context;
      this.items = items;
      this.layoutInflater = layoutInflater;
    }

    HackerNewsAdapter(Context context, LayoutInflater layoutInflater) {
      this(context, new ArrayList<Item>(), layoutInflater);
    }

    @Override public HackerNewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
      return new HackerNewsViewHolder(layoutInflater.inflate(R.layout.card_hn_news_item, viewGroup, false));
    }

    @Override public void onBindViewHolder(HackerNewsViewHolder holder, int position) {
      holder.textView.setText(items.get(position).title);
      if (colors.size() > 0) {
        // NOTE: when setting the card background color at API level 10 will crash!
        holder.cardView.setCardBackgroundColor(colors.get((int) (Math.random() * colors.size())));
      }

      holder.itemView.setTag(position);
      holder.itemView.setOnClickListener(this);
      holder.itemView.setOnLongClickListener(this);
    }

    public void add(Item item) {
      items.add(item);
      notifyItemInserted(items.indexOf(item));
    }

    public void addAll(List<Item> items) {
      this.items.addAll(items);
      notifyDataSetChanged();
    }

    public void clear() {
      if (!items.isEmpty()) {
        items.clear();
        notifyDataSetChanged();
      }
    }

    @Override public int getItemCount() {
      return items.size();
    }

    @Override public void onClick(View v) {
      int position = (int) v.getTag();
      Item item = items.get(position);
      context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(item.url)));
    }

    @Override public boolean onLongClick(View v) {
      Item item = items.get((Integer) v.getTag());
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType(context.getString(R.string.mime_text_plain));
      intent.putExtra(Intent.EXTRA_TITLE, item.title);
      intent.putExtra(Intent.EXTRA_TEXT, String.format("[%s] -> %s", item.title, item.url));
      context.startActivity(intent);
      return true;
    }
  }
}
