package com.johnpetitto.rebatefinder;

import com.google.gson.annotations.SerializedName;

public class StoreLocation {
  private int retailer_id;
  private double lat;
  @SerializedName("long") private double lng; // long is keyword in java

  public int retailerId() {
    return retailer_id;
  }

  public double lat() {
    return lat;
  }

  public double lng() {
    return lng;
  }
}
