package com.johnpetitto.rebatefinder.rebates;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.johnpetitto.rebatefinder.Offer;
import com.johnpetitto.rebatefinder.OfferResponse;
import com.johnpetitto.rebatefinder.Retailer;
import com.johnpetitto.rebatefinder.Utils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RebatesInteractor {
  private static Observable<Offer> offers;

  private RebatesPresenter presenter;

  public RebatesInteractor(RebatesPresenter presenter) {
    this.presenter = presenter;
  }

  public Observable<Offer> getOffers(final Retailer retailer) {
    if (offers == null) {
      offers = presenter.provideOfferData()
          .compose(new JsonParsingTransformer<OfferResponse>(OfferResponse.class))
          .flatMapObservable(new Func1<OfferResponse, Observable<? extends Offer>>() {
            @Override public Observable<? extends Offer> call(OfferResponse offerResponse) {
              return Observable.from(offerResponse.offers());
            }
          });
    }

    return offers.filter(new Func1<Offer, Boolean>() {
      @Override public Boolean call(Offer offer) {
        for (int retailerId : offer.retailers()) {
          if (retailerId == retailer.id()) {
            return true;
          }
        }
        return false;
      }
    });
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
