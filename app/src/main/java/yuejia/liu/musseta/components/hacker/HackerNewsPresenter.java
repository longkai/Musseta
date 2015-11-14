package yuejia.liu.musseta.components.hacker;

import android.text.TextUtils;
import android.widget.Toast;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * The hacker news items presenter.
 */
public class HackerNewsPresenter {
  private final HackerNewsActivity    activity;
  private final HackerNewsApi         hackerNewsApi;
  private final CompositeSubscription subscriptions;

  public HackerNewsPresenter(HackerNewsActivity activity, HackerNewsApi hackerNewsApi, CompositeSubscription subscriptions) {
    this.activity = activity;
    this.hackerNewsApi = hackerNewsApi;
    this.subscriptions = subscriptions;
  }

  public void present() {
    activity.showLoading(true);

    subscriptions.add(hackerNewsApi.topStories()
        .flatMap(longs -> Observable.from(longs))
        .take(50) // TODO: 11/8/15 further impl
        .map(id -> hackerNewsApi.item(id))
        .filter(item -> !item.dead && !item.deleted && !TextUtils.isEmpty(item.url))
        .doOnNext(item -> item.time = item.time * 1000)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Item>() {
          @Override public void onCompleted() {
            activity.showLoading(false);
          }

          @Override public void onError(Throwable e) {
            Timber.d(e, "loading fail!");
            activity.showLoading(false);
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
          }

          @Override public void onNext(Item item) {
            activity.appendItem(item);
          }
        })
    );
  }

  public void refresh() {
    present();
  }
}
