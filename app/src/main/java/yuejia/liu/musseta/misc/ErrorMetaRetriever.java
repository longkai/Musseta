package yuejia.liu.musseta.misc;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.util.Pair;

import retrofit.RetrofitError;
import timber.log.Timber;
import yuejia.liu.musseta.R;

/**
 * A simple error meta retriever.
 */
public class ErrorMetaRetriever {
  private final Resources res;

  public ErrorMetaRetriever(Context context) {
    this.res = context.getResources();
  }

  public Pair<String, Boolean> retrieve(Throwable e) {
    Timber.e(e, e.getMessage());

    boolean networkError = false;
    String message = null;
    if (e instanceof RetrofitError) {
      networkError = true;

      RetrofitError error = (RetrofitError) e;
      switch (error.getKind()) {
        case HTTP:
          message = res.getString(R.string.http_error, error.getResponse().getStatus());
          break;
        case NETWORK:
          message = res.getString(R.string.network_error);
          break;
        case CONVERSION:
          message = res.getString(R.string.conversion_error);
          break;
        case UNEXPECTED:
          message = res.getString(R.string.unexpected_error);
          break;
      }
    } else {
      message = res.getString(R.string.unexpected_error);
    }

    return new Pair<>(message, networkError);
  }
}
