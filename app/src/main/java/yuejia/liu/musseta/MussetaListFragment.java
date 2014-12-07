package yuejia.liu.musseta;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

/**
 * Musseta base list fragment.
 *
 * @author longkai
 */
public class MussetaListFragment extends ListFragment {
  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ((MussetaActivity) getActivity()).inject(this);
  }
}
