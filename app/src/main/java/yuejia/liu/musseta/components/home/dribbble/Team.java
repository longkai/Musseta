package yuejia.liu.musseta.components.home.dribbble;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

/**
 * The Dribbble Team.
 */
public class Team extends User implements Parcelable {
  public int members_count;
  public String members_url;
  public String team_shots_url;

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(this.members_count);
    dest.writeString(this.members_url);
    dest.writeString(this.team_shots_url);
  }

  public Team() {}

  protected Team(Parcel in) {
    super(in);
    this.members_count = in.readInt();
    this.members_url = in.readString();
    this.team_shots_url = in.readString();
  }

  public static final Creator<Team> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Team>() {
    @Override public Team createFromParcel(Parcel in, ClassLoader loader) {
      return new Team(in);
    }

    @Override public Team[] newArray(int size) {
      return new Team[size];
    }
  });
}
