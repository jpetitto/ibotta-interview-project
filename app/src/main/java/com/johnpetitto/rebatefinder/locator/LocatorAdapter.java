package com.johnpetitto.rebatefinder.locator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.johnpetitto.rebatefinder.R;
import com.johnpetitto.rebatefinder.Retailer;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

public class LocatorAdapter extends RecyclerView.Adapter<LocatorAdapter.VHItem> {
  private List<LocatorData> items = new ArrayList<>();
  private PublishSubject<Retailer> retailerClicks;

  public LocatorAdapter(PublishSubject<Retailer> retailerClicks) {
    this.retailerClicks = retailerClicks;
  }

  public void addItems(List<LocatorData> items) {
    this.items = items;
    notifyDataSetChanged();
  }

  @Override public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_location,
        parent, false);
    return new VHItem(itemView);
  }

  @Override public void onBindViewHolder(VHItem holder, int position) {
    final LocatorData item = items.get(position);
    holder.name.setText(item.retailer().name());

    String distance = String.format("%.1fm", item.distanceAway());
    holder.distance.setText(distance);

    Picasso.with(holder.logo.getContext())
        .load(item.retailer().iconUrl())
        .into(holder.logo);

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        retailerClicks.onNext(item.retailer());
      }
    });
  }

  @Override public int getItemCount() {
    return items.size();
  }

  class VHItem extends RecyclerView.ViewHolder {
    ImageView logo;
    TextView name;
    TextView distance;

    public VHItem(View itemView) {
      super(itemView);
      logo = (ImageView) itemView.findViewById(R.id.logo);
      name = (TextView) itemView.findViewById(R.id.name);
      distance = (TextView) itemView.findViewById(R.id.distance);
    }
  }
}
