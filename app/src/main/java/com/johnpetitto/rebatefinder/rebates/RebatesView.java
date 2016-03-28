package com.johnpetitto.rebatefinder.rebates;

import java.io.InputStream;
import rx.Single;

public interface RebatesView {
  Single<InputStream> provideOfferData();
}
