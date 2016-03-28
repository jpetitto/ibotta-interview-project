package com.johnpetitto.rebatefinder.locator;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.johnpetitto.rebatefinder.Retailer;
import com.johnpetitto.rebatefinder.RetailerResponse;
import com.johnpetitto.rebatefinder.StoreLocation;
import com.johnpetitto.rebatefinder.StoreLocationResponse;
import com.johnpetitto.rebatefinder.Utils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

  private static class JsonParsingTransformer<R> implements Single.Transformer<InputStream, R> {
    Type responseType;

    JsonParsingTransformer(Type responseType) {
      this.responseType = responseType;
    }

    @Override public Single<R> call(Single<InputStream> inputStreamSingle) {
      return inputStreamSingle.subscribeOn(AndroidSchedulers.mainThread())
          .observeOn(Schedulers.io())
          .map(new Func1<InputStream, JsonReader>() {
            @Override public JsonReader call(InputStream stream) {
              return new JsonReader(new InputStreamReader(stream));
            }
          })
          .map(new Func1<JsonReader, R>() {
            @Override public R call(JsonReader jsonReader) {
              R response = new Gson().fromJson(jsonReader, responseType);
              Utils.closeResourceQuietly(jsonReader);
              return response;
            }
          });
    }
  }
}
