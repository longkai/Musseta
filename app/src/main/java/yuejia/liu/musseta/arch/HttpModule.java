package yuejia.liu.musseta.arch;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

/**
 * Http module.
 *
 * @author longkai
 */
@Module(
    includes = ApiModule.class,
    complete = false,
    library = true
)
public class HttpModule {
  @Provides @Singleton OkHttpClient provideOkHttpClient(Application app) {
    OkHttpClient client = new OkHttpClient();
    try {
      final String dir = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
          || !Environment.isExternalStorageRemovable()
          ? app.getExternalCacheDir().getPath()
          : app.getCacheDir().getPath();
      client.setCache(new Cache(new File(dir + File.separator + "http"), 50L * 1024 * 1024));
    } catch (IOException e) {
      Timber.e(e, "create http cache dir error.");
    }
    return client;
  }

  @Provides @Singleton Picasso providePicasso(Application app, OkHttpClient client) {
    return new Picasso.Builder(app)
        .downloader(new OkHttpDownloader(client))
        .listener(new Picasso.Listener() {
          @Override public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
            Timber.e(exception, "fail to download image: %s", uri);
          }
        }).build();
  }
}
