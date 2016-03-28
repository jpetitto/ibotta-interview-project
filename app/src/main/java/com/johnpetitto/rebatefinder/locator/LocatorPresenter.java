package com.johnpetitto.rebatefinder.locator;

import com.johnpetitto.rebatefinder.Retailer;
import com.johnpetitto.rebatefinder.StoreLocation;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

public class LocatorPresenter {
  private LocatorView view;
  private LocatorInteractor interactor;

  public LocatorPresenter(LocatorView view) {
    this.view = view;
    interactor = new LocatorInteractor(this);
  }

  public Observable<LocatorData> getLocatorData() {
    return interactor.getRetailers()
        .toList()
        .map(createRetailerMapping())
        .flatMap(mergeLocationData());
  }

  public Single<InputStream> provideRetailerData() {
    return view.provideRetailerData();
  }

  public Single<InputStream> provideStoreLocationData() {
    return view.provideStoreLocationData();
  }

  private Func1<List<Retailer>, Map<Integer, Retailer>> createRetailerMapping() {
    return new Func1<List<Retailer>, Map<Integer, Retailer>>() {
      @Override public Map<Integer, Retailer> call(List<Retailer> retailers) {
        Map<Integer, Retailer> retailerMap = new HashMap<>();
        for (Retailer retailer : retailers) {
          retailerMap.put(retailer.id(), retailer);
        }
        return retailerMap;
      }
    };
  }

  private Func1<Map<Integer, Retailer>, Observable<LocatorData>> mergeLocationData() {
    return new Func1<Map<Integer, Retailer>, Observable<LocatorData>>() {
      @Override public Observable<LocatorData> call(Map<Integer, Retailer> retailerMap) {
        return interactor.getStoreLocations()
            .filter(omitStoresWithoutRetailer(retailerMap))
            .map(createRetailerLocationData(retailerMap));
      }
    };
  }

  private Func1<StoreLocation, Boolean> omitStoresWithoutRetailer(
      final Map<Integer, Retailer> retailerMap) {
    return new Func1<StoreLocation, Boolean>() {
      @Override public Boolean call(StoreLocation location) {
        return retailerMap.containsKey(location.retailerId());
      }
    };
  }

  private Func1<StoreLocation, LocatorData> createRetailerLocationData(
      final Map<Integer, Retailer> retailerMap) {
    return new Func1<StoreLocation, LocatorData>() {
      @Override public LocatorData call(StoreLocation location) {
        Retailer retailer = retailerMap.get(location.retailerId());
        return new LocatorData(retailer, location);
      }
    };
  }
}
