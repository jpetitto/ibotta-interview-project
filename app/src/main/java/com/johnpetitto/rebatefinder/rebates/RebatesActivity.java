package com.johnpetitto.rebatefinder.rebates;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import com.johnpetitto.rebatefinder.Offer;
import com.johnpetitto.rebatefinder.R;
import com.johnpetitto.rebatefinder.Retailer;
import com.johnpetitto.rebatefinder.Utils;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class RebatesActivity extends AppCompatActivity implements RebatesView {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rebates);

    Retailer retailer = getIntent().getExtras().getParcelable("retailer");

    setTitle(retailer.name());
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    PublishSubject<Offer> offerClicks = PublishSubject.create();
    offerClicks.subscribe(new Action1<Offer>() {
      @Override public void call(Offer offer) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(offer.shareUrl()));
        startActivity(intent);
      }
    });

    RecyclerView rebateList = (RecyclerView) findViewById(R.id.rebate_list);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    rebateList.setLayoutManager(layoutManager);
    final RebateAdapter rebateAdapter = new RebateAdapter(offerClicks);
    rebateList.setAdapter(rebateAdapter);

    new RebatesPresenter(this).getOffers(retailer)
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Offer>>() {
          @Override public void call(List<Offer> offers) {
            rebateAdapter.addItems(offers);
          }
        });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public Single<InputStream> provideOfferData() {
    return Single.defer(new Callable<Single<InputStream>>() {
      @Override public Single<InputStream> call() throws Exception {
        return Single.just(Utils.openStreamQuietly(getAssets(), "Offers.json"));
      }
    });
  }
}
