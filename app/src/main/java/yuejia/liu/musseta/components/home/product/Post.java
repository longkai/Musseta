package yuejia.liu.musseta.components.home.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

import com.google.gson.annotations.SerializedName;

/**
 * Post entity.
 */
public class Post implements Parcelable {
  public long           category_id;
  public String         day;
  public long           id;
  public String         name;
  public String         product_state;
  public String         tagline;
  public int            comments_count;
  public Date           created_at;
  // drop current_user
  public String         discussion_url;
  public boolean        featured;
  public boolean        maker_inside;
  public List<User>     makers;
  public List<Platform> platforms;
  public String         redirect_url;
  public Screenshot_url screenshot_url;
  public User           user;
  public int            votes_count;

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.category_id);
    dest.writeString(this.day);
    dest.writeLong(this.id);
    dest.writeString(this.name);
    dest.writeString(this.product_state);
    dest.writeString(this.tagline);
    dest.writeInt(this.comments_count);
    dest.writeLong(created_at != null ? created_at.getTime() : -1);
    dest.writeString(this.discussion_url);
    dest.writeByte(featured ? (byte) 1 : (byte) 0);
    dest.writeByte(maker_inside ? (byte) 1 : (byte) 0);
    dest.writeList(this.makers);
    dest.writeTypedList(platforms);
    dest.writeString(this.redirect_url);
    dest.writeParcelable(this.screenshot_url, 0);
    dest.writeParcelable(this.user, flags);
    dest.writeInt(this.votes_count);
  }

  public Post() {}

  protected Post(Parcel in) {
    this.category_id = in.readLong();
    this.day = in.readString();
    this.id = in.readLong();
    this.name = in.readString();
    this.product_state = in.readString();
    this.tagline = in.readString();
    this.comments_count = in.readInt();
    long tmpCreated_at = in.readLong();
    this.created_at = tmpCreated_at == -1 ? null : new Date(tmpCreated_at);
    this.discussion_url = in.readString();
    this.featured = in.readByte() != 0;
    this.maker_inside = in.readByte() != 0;
    this.makers = new ArrayList<User>();
    in.readList(this.makers, List.class.getClassLoader());
    this.platforms = in.createTypedArrayList(Platform.CREATOR);
    this.redirect_url = in.readString();
    this.screenshot_url = in.readParcelable(Screenshot_url.class.getClassLoader());
    this.user = in.readParcelable(User.class.getClassLoader());
    this.votes_count = in.readInt();
  }

  public static final Creator<Post> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Post>() {
    @Override public Post createFromParcel(Parcel in, ClassLoader loader) {
      return new Post(in);
    }

    @Override public Post[] newArray(int size) {
      return new Post[size];
    }
  });

  public static class Screenshot_url implements Parcelable {
    @SerializedName("300px")
    public String _300px;
    @SerializedName("850px")
    public String _850px;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this._300px);
      dest.writeString(this._850px);
    }

    public Screenshot_url() {}

    protected Screenshot_url(Parcel in) {
      this._300px = in.readString();
      this._850px = in.readString();
    }

    public static final Creator<Screenshot_url> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Screenshot_url>() {
      @Override public Screenshot_url createFromParcel(Parcel in, ClassLoader loader) {
        return new Screenshot_url(in);
      }

      @Override public Screenshot_url[] newArray(int size) {
        return new Screenshot_url[size];
      }
    });
  }

  public static class Platform implements Parcelable {
    public String name;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.name);}

    public Platform() {}

    protected Platform(Parcel in) {this.name = in.readString();}

    public static final Creator<Platform> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<Platform>() {
      @Override public Platform createFromParcel(Parcel in, ClassLoader loader) {
        return new Platform(in);
      }

      @Override public Platform[] newArray(int size) {
        return new Platform[size];
      }
    });
  }

  public static class PostsRequestBody {
    public int days_ago;
    /** Alternate parameter for requesting specific days (Format: day=YYYY-MM-DD) */
//    public int day;
  }
}
