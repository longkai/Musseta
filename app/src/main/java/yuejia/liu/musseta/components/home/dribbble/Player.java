package yuejia.liu.musseta.components.home.dribbble;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

/**
 * The Dribbble user.
 */
public class Player extends User implements Parcelable {
  public int    teams_count;
  public String teams_url;

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.teams_count);
    dest.writeString(this.teams_url);
  }

  public Player() {}

  protected Player(Parcel in) {
    this.teams_count = in.readInt();
    this.teams_url = in.readString();
  }

  public static final Creator<Player> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Player>() {
    @Override public Player createFromParcel(Parcel in, ClassLoader loader) {
      return new Player(in);
    }

    @Override public Player[] newArray(int size) {
      return new Player[size];
    }
  });
}
