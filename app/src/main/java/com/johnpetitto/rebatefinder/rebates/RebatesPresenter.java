package com.johnpetitto.rebatefinder.rebates;

import com.johnpetitto.rebatefinder.Offer;
import com.johnpetitto.rebatefinder.Retailer;
import java.io.InputStream;
import rx.Observable;
import rx.Single;

public class RebatesPresenter {
  private RebatesView view;
  private RebatesInteractor interactor;

  public RebatesPresenter(RebatesView view) {
    this.view = view;
    interactor = new RebatesInteractor(this);
  }

  public Observable<Offer> getOffers(Retailer retailer) {
    return interactor.getOffers(retailer);
  }

  public Single<InputStream> provideOfferData() {
    return view.provideOfferData();
  }
}
