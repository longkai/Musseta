package yuejia.liu.musseta.model.hn;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * HN user model.
 *
 * @author longkai
 */
public class User implements Parcelable {
  public String id;
  public int    delay;
  public long   created;
  public int    karma;
  public String about;
  public long[] submitted;

  public User() {}

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeInt(this.delay);
    dest.writeLong(this.created);
    dest.writeInt(this.karma);
    dest.writeString(this.about);
    dest.writeLongArray(this.submitted);
  }

  private User(Parcel in) {
    this.id = in.readString();
    this.delay = in.readInt();
    this.created = in.readLong();
    this.karma = in.readInt();
    this.about = in.readString();
    this.submitted = in.createLongArray();
  }

  public static final Creator<User> CREATOR = new Creator<User>() {
    public User createFromParcel(Parcel source) {return new User(source);}

    public User[] newArray(int size) {return new User[size];}
  };
}
