package yuejia.liu.musseta;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Musseta base fragment.
 *
 * @author longkai
 */
public class MussetaFragment extends Fragment {
  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ((MussetaActivity) getActivity()).inject(this);
  }
}
