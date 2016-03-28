package com.johnpetitto.rebatefinder.locator;

import com.johnpetitto.rebatefinder.Retailer;
import com.johnpetitto.rebatefinder.StoreLocation;

public class LocatorData {
  private final Retailer retailer;
  private final StoreLocation location;
  private double distanceAway;

  public LocatorData(Retailer retailer, StoreLocation location) {
    this.retailer = retailer;
    this.location = location;
  }

  public Retailer retailer() {
    return retailer;
  }

  public StoreLocation location() {
    return location;
  }

  public void distanceAway(double distance) {
    distanceAway = distance;
  }

  public double distanceAway() {
    return distanceAway;
  }
}
