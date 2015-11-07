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
}
