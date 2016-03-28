package com.johnpetitto.rebatefinder.locator;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout.LayoutParams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.johnpetitto.rebatefinder.R;
import com.johnpetitto.rebatefinder.Retailer;
import com.johnpetitto.rebatefinder.StoreLocation;
import com.johnpetitto.rebatefinder.Utils;
import com.johnpetitto.rebatefinder.rebates.RebatesActivity;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class LocatorActivity extends AppCompatActivity implements LocatorView,
    OnMapReadyCallback {
  private static final LatLng LOCATION = new LatLng(39.8288, -104.987);
  private static final double RADIUS = 5000.0;

  private LocatorPresenter presenter;
  private GoogleMap map;
  private LocatorAdapter locatorAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_locator);

    presenter = new LocatorPresenter(this);

    PublishSubject<Retailer> retailerClicks = PublishSubject.create();
    retailerClicks.subscribe(new Action1<Retailer>() {
      @Override public void call(Retailer retailer) {
        Intent intent = new Intent(LocatorActivity.this, RebatesActivity.class);
        intent.putExtra("retailer", retailer);
        startActivity(intent);
      }
    });

    final RecyclerView locationList = (RecyclerView) findViewById(R.id.location_list);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    locationList.setLayoutManager(layoutManager);
    locatorAdapter = new LocatorAdapter(retailerClicks);
    locationList.setAdapter(locatorAdapter);

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.setRetainInstance(true);

    // make map height 1/3 of available screen size
    Point screenSize = new Point();
    getWindowManager().getDefaultDisplay().getSize(screenSize);
    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, screenSize.y / 3);
    findViewById(R.id.map_area).setLayoutParams(params);

    mapFragment.getMapAsync(this);
  }

  @Override public void onMapReady(GoogleMap map) {
    this.map = map;
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCATION, 10.0f));

    presenter.getLocatorData()
        .filter(omitLocationsNotInRange())
        .toList()
        .map(sortByDistance())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(displayMarkerForLocation());
  }

  @Override
  public Single<InputStream> provideRetailerData() {
    return Single.defer(new Callable<Single<InputStream>>() {
      @Override public Single<InputStream> call() throws Exception {
        return Single.just(Utils.openStreamQuietly(getAssets(), "Retailers.json"));
      }
    });
  }

  @Override
  public Single<InputStream> provideStoreLocationData() {
    return Single.defer(new Callable<Single<InputStream>>() {
      @Override public Single<InputStream> call() throws Exception {
        return Single.just(Utils.openStreamQuietly(getAssets(), "StoreLocations.json"));
      }
    });
  }

  private Func1<LocatorData, Boolean> omitLocationsNotInRange() {
    return new Func1<LocatorData, Boolean>() {
      @Override public Boolean call(LocatorData locatorData) {
        StoreLocation location = locatorData.location();
        LatLng position = new LatLng(location.lat(), location.lng());
        double distance = SphericalUtil.computeDistanceBetween(LOCATION, position);
        locatorData.distanceAway(distance);
        return distance < RADIUS;
      }
    };
  }

  private Func1<List<LocatorData>, List<LocatorData>> sortByDistance() {
    return new Func1<List<LocatorData>, List<LocatorData>>() {
      @Override
      public List<LocatorData> call(List<LocatorData> locatorDatas) {
        Collections.sort(locatorDatas, new Comparator<LocatorData>() {
          @Override public int compare(LocatorData lhs, LocatorData rhs) {
            return (int) (lhs.distanceAway() - rhs.distanceAway());
          }
        });
        return locatorDatas;
      }
    };
  }

  private Action1<List<LocatorData>> displayMarkerForLocation() {
    return new Action1<List<LocatorData>>() {
      @Override public void call(List<LocatorData> locatorDatas) {
        for (LocatorData locatorData : locatorDatas) {
          Retailer retailer = locatorData.retailer();
          StoreLocation location = locatorData.location();

          map.addMarker(new MarkerOptions()
              .position(new LatLng(location.lat(), location.lng()))
              .title(retailer.name()));
        }

        locatorAdapter.addItems(locatorDatas);
      }
    };
  }
}
