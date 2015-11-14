package yuejia.liu.musseta.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A simple network watcher
 */
public class NetworkWatcher {
  private final ConnectivityManager cm;

  public NetworkWatcher(Context context) {
    cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  public boolean hasNetwork() {
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  }

  public boolean hasWifi() {
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

    return activeNetwork != null && activeNetwork.isConnectedOrConnecting() &&
        activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
  }
}
