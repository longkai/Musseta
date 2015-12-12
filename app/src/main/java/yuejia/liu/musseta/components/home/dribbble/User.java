package yuejia.liu.musseta.components.home.dribbble;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

/**
 * The Dribbble user.
 */
public class User implements Parcelable {
  public long    id;
  public String  name;
  public String  username;
  public String  html_url;
  public String  avatar_url;
  public String  bio; // html text
  public String  location;
  public Link    links;
  public int     buckets_count;
  public int     comments_received_count;
  public int     followers_count;
  public int     followings_count;
  public int     likes_count;
  public int     likes_received_count;
  public int     projects_count;
  public int     rebounds_received_count;
  public int     shots_count;
  public boolean can_upload_shot;
  public String  type;
  public boolean pro;
  public String  buckets_url;
  public String  followers_url;
  public String  following_url;
  public String  likes_url;
  public String  shots_url;
  public Date    created_at;
  public Date    updated_at;

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeString(this.name);
    dest.writeString(this.username);
    dest.writeString(this.html_url);
    dest.writeString(this.avatar_url);
    dest.writeString(this.bio);
    dest.writeString(this.location);
    dest.writeParcelable(this.links, 0);
    dest.writeInt(this.buckets_count);
    dest.writeInt(this.comments_received_count);
    dest.writeInt(this.followers_count);
    dest.writeInt(this.followings_count);
    dest.writeInt(this.likes_count);
    dest.writeInt(this.likes_received_count);
    dest.writeInt(this.projects_count);
    dest.writeInt(this.rebounds_received_count);
    dest.writeInt(this.shots_count);
    dest.writeByte(can_upload_shot ? (byte) 1 : (byte) 0);
    dest.writeString(this.type);
    dest.writeByte(pro ? (byte) 1 : (byte) 0);
    dest.writeString(this.buckets_url);
    dest.writeString(this.followers_url);
    dest.writeString(this.following_url);
    dest.writeString(this.likes_url);
    dest.writeString(this.shots_url);
    dest.writeLong(created_at != null ? created_at.getTime() : -1);
    dest.writeLong(updated_at != null ? updated_at.getTime() : -1);
  }

  public User() {}

  protected User(Parcel in) {
    this.id = in.readLong();
    this.name = in.readString();
    this.username = in.readString();
    this.html_url = in.readString();
    this.avatar_url = in.readString();
    this.bio = in.readString();
    this.location = in.readString();
    this.links = in.readParcelable(Link.class.getClassLoader());
    this.buckets_count = in.readInt();
    this.comments_received_count = in.readInt();
    this.followers_count = in.readInt();
    this.followings_count = in.readInt();
    this.likes_count = in.readInt();
    this.likes_received_count = in.readInt();
    this.projects_count = in.readInt();
    this.rebounds_received_count = in.readInt();
    this.shots_count = in.readInt();
    this.can_upload_shot = in.readByte() != 0;
    this.type = in.readString();
    this.pro = in.readByte() != 0;
    this.buckets_url = in.readString();
    this.followers_url = in.readString();
    this.following_url = in.readString();
    this.likes_url = in.readString();
    this.shots_url = in.readString();
    long tmpCreated_at = in.readLong();
    this.created_at = tmpCreated_at == -1 ? null : new Date(tmpCreated_at);
    long tmpUpdated_at = in.readLong();
    this.updated_at = tmpUpdated_at == -1 ? null : new Date(tmpUpdated_at);
  }

  public static final Creator<User> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<User>() {
    @Override public User createFromParcel(Parcel in, ClassLoader loader) {
      return new User(in);
    }

    @Override public User[] newArray(int size) {
      return new User[size];
    }
  });

  public static class Link implements Parcelable {
    public String web;
    public String twitter;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.web);
      dest.writeString(this.twitter);
    }

    public Link() {}

    protected Link(Parcel in) {
      this.web = in.readString();
      this.twitter = in.readString();
    }

    public static final Creator<Link> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Link>() {
      @Override public Link createFromParcel(Parcel in, ClassLoader loader) {
        return new Link(in);
      }

      @Override public Link[] newArray(int size) {
        return new Link[size];
      }
    });
  }
}
