package com.johnpetitto.rebatefinder.rebates;

import com.johnpetitto.rebatefinder.JsonParsingTransformer;
import com.johnpetitto.rebatefinder.Offer;
import com.johnpetitto.rebatefinder.OfferResponse;
import com.johnpetitto.rebatefinder.Retailer;
import rx.Observable;
import rx.functions.Func1;

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
}
