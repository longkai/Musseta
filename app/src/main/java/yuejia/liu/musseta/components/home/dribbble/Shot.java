package yuejia.liu.musseta.components.home.dribbble;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

/**
 * Shot, like Dribbble' s tweet.
 */
public class Shot implements Parcelable {
  public long id;
  public String title;
  public String description; // html text?
  public int width;
  public int height;
  public Images images;
  public int views_count;
  public int likes_count;
  public int comments_count;
  public int attachments_count;
  public int rebounds_count;
  public int buckets_count;
  public Date created_at;
  public Date updated_at;
  public String html_url;
  public String attachments_url;
  public String buckets_url;
  public String comments_url;
  public String likes_url;
  public String projects_url;
  public String rebounds_url;
  public boolean animated;
  public String[] tags;
  public Player user;
  public Team team;

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeString(this.title);
    dest.writeString(this.description);
    dest.writeInt(this.width);
    dest.writeInt(this.height);
    dest.writeParcelable(this.images, 0);
    dest.writeInt(this.views_count);
    dest.writeInt(this.likes_count);
    dest.writeInt(this.comments_count);
    dest.writeInt(this.attachments_count);
    dest.writeInt(this.rebounds_count);
    dest.writeInt(this.buckets_count);
    dest.writeLong(created_at != null ? created_at.getTime() : -1);
    dest.writeLong(updated_at != null ? updated_at.getTime() : -1);
    dest.writeString(this.html_url);
    dest.writeString(this.attachments_url);
    dest.writeString(this.buckets_url);
    dest.writeString(this.comments_url);
    dest.writeString(this.likes_url);
    dest.writeString(this.projects_url);
    dest.writeString(this.rebounds_url);
    dest.writeByte(animated ? (byte) 1 : (byte) 0);
    dest.writeStringArray(this.tags);
    dest.writeParcelable(this.user, 0);
    dest.writeParcelable(this.team, 0);
  }

  public Shot() {}

  protected Shot(Parcel in) {
    this.id = in.readLong();
    this.title = in.readString();
    this.description = in.readString();
    this.width = in.readInt();
    this.height = in.readInt();
    this.images = in.readParcelable(Images.class.getClassLoader());
    this.views_count = in.readInt();
    this.likes_count = in.readInt();
    this.comments_count = in.readInt();
    this.attachments_count = in.readInt();
    this.rebounds_count = in.readInt();
    this.buckets_count = in.readInt();
    long tmpCreated_at = in.readLong();
    this.created_at = tmpCreated_at == -1 ? null : new Date(tmpCreated_at);
    long tmpUpdated_at = in.readLong();
    this.updated_at = tmpUpdated_at == -1 ? null : new Date(tmpUpdated_at);
    this.html_url = in.readString();
    this.attachments_url = in.readString();
    this.buckets_url = in.readString();
    this.comments_url = in.readString();
    this.likes_url = in.readString();
    this.projects_url = in.readString();
    this.rebounds_url = in.readString();
    this.animated = in.readByte() != 0;
    this.tags = in.createStringArray();
    this.user = in.readParcelable(Player.class.getClassLoader());
    this.team = in.readParcelable(Team.class.getClassLoader());
  }

  public static final Creator<Shot> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Shot>() {
    @Override public Shot createFromParcel(Parcel in, ClassLoader loader) {
      return new Shot(in);
    }

    @Override public Shot[] newArray(int size) {
      return new Shot[size];
    }
  });

  public static class Images implements Parcelable {
    public String hidpi; // may null?
    public String normal;
    public String teaser;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.hidpi);
      dest.writeString(this.normal);
      dest.writeString(this.teaser);
    }

    public Images() {}

    protected Images(Parcel in) {
      this.hidpi = in.readString();
      this.normal = in.readString();
      this.teaser = in.readString();
    }

    public static final Creator<Images> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Images>() {
      @Override public Images createFromParcel(Parcel in, ClassLoader loader) {
        return new Images(in);
      }

      @Override public Images[] newArray(int size) {
        return new Images[size];
      }
    });
  }
}
