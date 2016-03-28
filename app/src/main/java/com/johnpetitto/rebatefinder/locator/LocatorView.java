package com.johnpetitto.rebatefinder.locator;

import java.io.InputStream;
import rx.Single;

public interface LocatorView {
  Single<InputStream> provideRetailerData();
  Single<InputStream> provideStoreLocationData();
}
