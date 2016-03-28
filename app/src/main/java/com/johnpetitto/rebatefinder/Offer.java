package com.johnpetitto.rebatefinder;

public class Offer {
  private String name;
  private String description;
  private String expiration;
  private String url;
  private String share_url;
  private int[] retailers;

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  public String expiration() {
    return expiration;
  }

  public String imageUrl() {
    return url;
  }

  public String shareUrl() {
    return share_url;
  }

  public int[] retailers() {
    return retailers;
  }
}
