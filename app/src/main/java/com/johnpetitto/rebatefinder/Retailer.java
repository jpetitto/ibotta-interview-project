package com.johnpetitto.rebatefinder;

import android.os.Parcel;
import android.os.Parcelable;

public class Retailer implements Parcelable {
  private int id;
  private String name;
  private String icon_url;

  protected Retailer(Parcel in) {
    id = in.readInt();
    name = in.readString();
    icon_url = in.readString();
  }

  public int id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String iconUrl() {
    return icon_url;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(name);
    dest.writeString(icon_url);
  }

  public static final Creator<Retailer> CREATOR = new Creator<Retailer>() {
    @Override public Retailer createFromParcel(Parcel in) {
      return new Retailer(in);
    }

    @Override public Retailer[] newArray(int size) {
      return new Retailer[size];
    }
  };
}
