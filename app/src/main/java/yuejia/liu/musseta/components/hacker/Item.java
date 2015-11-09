package yuejia.liu.musseta.components.hacker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * HN item model.
 */
public class Item implements Parcelable {
  public long    id;
  public boolean deleted;
  public String  type;
  public String  by;
  public long    time;
  public String  text;
  public boolean dead;
  public long    parent;
  public long[]  kids;
  public String  url;
  public int     score;
  public String  title;
  public long[]  parts;

  public Item() {}

  private Item(Builder builder) {
    id = builder.id;
    deleted = builder.deleted;
    type = builder.type;
    by = builder.by;
    time = builder.time;
    text = builder.text;
    dead = builder.dead;
    parent = builder.parent;
    kids = builder.kids;
    url = builder.url;
    score = builder.score;
    title = builder.title;
    parts = builder.parts;
  }

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeByte(deleted ? (byte) 1 : (byte) 0);
    dest.writeString(this.type);
    dest.writeString(this.by);
    dest.writeLong(this.time);
    dest.writeString(this.text);
    dest.writeByte(dead ? (byte) 1 : (byte) 0);
    dest.writeLong(this.parent);
    dest.writeLongArray(this.kids);
    dest.writeString(this.url);
    dest.writeInt(this.score);
    dest.writeString(this.title);
    dest.writeLongArray(this.parts);
  }

  private Item(Parcel in) {
    this.id = in.readLong();
    this.deleted = in.readByte() != 0;
    this.type = in.readString();
    this.by = in.readString();
    this.time = in.readLong();
    this.text = in.readString();
    this.dead = in.readByte() != 0;
    this.parent = in.readLong();
    this.kids = in.createLongArray();
    this.url = in.readString();
    this.score = in.readInt();
    this.title = in.readString();
    this.parts = in.createLongArray();
  }

  public static final Creator<Item> CREATOR = new Creator<Item>() {
    public Item createFromParcel(Parcel source) {return new Item(source);}

    public Item[] newArray(int size) {return new Item[size];}
  };

  public static final class Builder {
    private long id;
    private boolean deleted;
    private String type;
    private String by;
    private long time;
    private String text;
    private boolean dead;
    private long parent;
    private long[] kids;
    private String url;
    private int score;
    private String title;
    private long[] parts;

    public Builder() {}

    public Builder id(long val) {
      id = val;
      return this;
    }

    public Builder deleted(boolean val) {
      deleted = val;
      return this;
    }

    public Builder type(String val) {
      type = val;
      return this;
    }

    public Builder by(String val) {
      by = val;
      return this;
    }

    public Builder time(long val) {
      time = val;
      return this;
    }

    public Builder text(String val) {
      text = val;
      return this;
    }

    public Builder dead(boolean val) {
      dead = val;
      return this;
    }

    public Builder parent(long val) {
      parent = val;
      return this;
    }

    public Builder kids(long[] val) {
      kids = val;
      return this;
    }

    public Builder url(String val) {
      url = val;
      return this;
    }

    public Builder score(int val) {
      score = val;
      return this;
    }

    public Builder title(String val) {
      title = val;
      return this;
    }

    public Builder parts(long[] val) {
      parts = val;
      return this;
    }

    public Item build() {return new Item(this);}
  }
}
