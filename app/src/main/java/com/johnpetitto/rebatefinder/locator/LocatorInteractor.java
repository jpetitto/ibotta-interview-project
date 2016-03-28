package com.johnpetitto.rebatefinder.locator;

import com.johnpetitto.rebatefinder.JsonParsingTransformer;
import com.johnpetitto.rebatefinder.Retailer;
import com.johnpetitto.rebatefinder.RetailerResponse;
import com.johnpetitto.rebatefinder.StoreLocation;
import com.johnpetitto.rebatefinder.StoreLocationResponse;
import rx.Observable;
import rx.functions.Func1;

public class LocatorInteractor {
  private static Observable<Retailer> retailers;
  private static Observable<StoreLocation> storeLocations;

  private LocatorPresenter presenter;

  public LocatorInteractor(LocatorPresenter presenter) {
    this.presenter = presenter;
  }

  public Observable<Retailer> getRetailers() {
    if (retailers == null) {
      retailers = presenter.provideRetailerData()
          .compose(new JsonParsingTransformer<RetailerResponse>(RetailerResponse.class))
          .flatMapObservable(new Func1<RetailerResponse, Observable<Retailer>>() {
            @Override public Observable<Retailer> call(RetailerResponse retailerResponse) {
              return Observable.from(retailerResponse.retailers());
            }
          });
    }

    return retailers;
  }

  public Observable<StoreLocation> getStoreLocations() {
    if (storeLocations == null) {
      storeLocations = presenter.provideStoreLocationData()
          .compose(new JsonParsingTransformer<StoreLocationResponse>(StoreLocationResponse.class))
          .flatMapObservable(new Func1<StoreLocationResponse, Observable<StoreLocation>>() {
            @Override
            public Observable<StoreLocation> call(StoreLocationResponse storeLocationResponse) {
              return Observable.from(storeLocationResponse.stores());
            }
          });
    }

    return storeLocations;
  }
}
