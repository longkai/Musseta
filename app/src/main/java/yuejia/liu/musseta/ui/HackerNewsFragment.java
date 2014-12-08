package yuejia.liu.musseta.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;
import yuejia.liu.musseta.MussetaListFragment;
import yuejia.liu.musseta.R;
import yuejia.liu.musseta.api.hn.HackerNewsApi;
import yuejia.liu.musseta.model.hn.Item;

import javax.inject.Inject;

/**
 * Simple Hacker News list.
 *
 * @author longkai
 */
public class HackerNewsFragment extends MussetaListFragment implements SwipeRefreshLayout.OnRefreshListener {
  @Inject HackerNewsApi hackerNewsApi;

  private Subscription listRequest = Subscriptions.empty();
  private Subscription itemRequest = Subscriptions.empty();

  private ArrayAdapter<Item> newsAdapter;

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getSwipeRefreshLayout().setColorSchemeResources(
        material.palette.R.color.material_blue_500,
        material.palette.R.color.material_red_500,
        material.palette.R.color.material_lime_500,
        material.palette.R.color.material_green_500
    );
    getSwipeRefreshLayout().setOnRefreshListener(this);

    newsAdapter = new ArrayAdapter<Item>(getActivity(),
        android.R.layout.simple_list_item_1, android.R.id.text1) {
      @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
          convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_1,
              parent, false);
        }
        ((TextView) convertView).setText(getItem(position).title);
        return convertView;
      }
    };

    performItemsRequest();
  }

  private void performItemsRequest() {
    listRequest = hackerNewsApi.topStories()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<long[]>() {
          @Override public void onCompleted() {
            setListAdapter(newsAdapter);
            getSwipeRefreshLayout().setRefreshing(false);
            registerForContextMenu(getListView());
          }

          @Override public void onError(Throwable e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            getSwipeRefreshLayout().setRefreshing(false);
          }

          @Override public void onNext(long[] longs) {
            unregisterForContextMenu(getListView());
            newsAdapter.clear();
            for (long id : longs) {
              hackerNewsApi.item(id).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Item>() {
                @Override public void call(Item item) {
                  newsAdapter.add(item);
                }
              });
            }
          }
        });
  }

  @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    menu.add(Menu.NONE, android.R.id.button1, Menu.NONE, getString(R.string.share));
  }

  @Override public boolean onContextItemSelected(MenuItem item) {
    AbsListView.AdapterContextMenuInfo menuInfo = (AbsListView.AdapterContextMenuInfo) item.getMenuInfo();
    Item _item = newsAdapter.getItem(menuInfo.position);
    switch (item.getItemId()) {
      case android.R.id.button1:
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(getString(R.string.mime_text_plain));
        intent.putExtra(Intent.EXTRA_TITLE, _item.title);
        intent.putExtra(Intent.EXTRA_TEXT, String.format("[%s] -> %s", _item.title, _item.url));
        startActivity(intent);
        return true;
      default:
        return super.onContextItemSelected(item);
    }
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(newsAdapter.getItem(position).url)));
  }

  @Override public void onDestroyView() {
    itemRequest.unsubscribe();
    listRequest.unsubscribe();
    super.onDestroyView();
  }

  @Override public void onRefresh() {
    performItemsRequest();
  }
}
