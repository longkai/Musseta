package yuejia.liu.musseta.components.home.product;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

/**
 * User entity.
 */
public class User implements Parcelable {
  public String    id;
  public String    name;
  public String    headline;
  public Date      created_at;
  public String    username;
  public String    twitter_username;
  public String    website_url;
  public Image_url image_url;
  public String    profile_url;

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeString(this.headline);
    dest.writeLong(created_at != null ? created_at.getTime() : -1);
    dest.writeString(this.username);
    dest.writeString(this.twitter_username);
    dest.writeString(this.website_url);
    dest.writeParcelable(this.image_url, flags);
    dest.writeString(this.profile_url);
  }

  public User() {}

  protected User(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
    this.headline = in.readString();
    long tmpCreated_at = in.readLong();
    this.created_at = tmpCreated_at == -1 ? null : new Date(tmpCreated_at);
    this.username = in.readString();
    this.twitter_username = in.readString();
    this.website_url = in.readString();
    this.image_url = in.readParcelable(Image_url.class.getClassLoader());
    this.profile_url = in.readString();
  }

  public static final Creator<User> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<User>() {
    @Override public User createFromParcel(Parcel in, ClassLoader loader) {
      return new User(in);
    }

    @Override public User[] newArray(int size) {
      return new User[size];
    }
  });

  public static class Image_url implements Parcelable {
    // TODO: 12/9/15 consider other dimen?
    public String original;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.original);}

    public Image_url() {}

    protected Image_url(Parcel in) {this.original = in.readString();}

    public static final Creator<Image_url> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Image_url>() {
      @Override public Image_url createFromParcel(Parcel in, ClassLoader loader) {
        return new Image_url(in);
      }

      @Override public Image_url[] newArray(int size) {
        return new Image_url[size];
      }
    });
  }
}
