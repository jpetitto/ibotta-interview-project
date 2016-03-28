package com.johnpetitto.rebatefinder.rebates;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.johnpetitto.rebatefinder.Offer;
import com.johnpetitto.rebatefinder.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

public class RebateAdapter extends RecyclerView.Adapter<RebateAdapter.VHItem> {
  private List<Offer> items = new ArrayList<>();
  private PublishSubject<Offer> offerClicks;

  public RebateAdapter(PublishSubject<Offer> offerClicks) {
    this.offerClicks = offerClicks;
  }

  public void addItems(List<Offer> items) {
    this.items = items;
    notifyDataSetChanged();
  }

  @Override public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_rebates,
        parent, false);
    return new VHItem(itemView);
  }

  @Override public void onBindViewHolder(VHItem holder, int position) {
    final Offer offer = items.get(position);
    holder.name.setText(offer.name());
    holder.description.setText(offer.description());
    holder.expiration.setText(offer.expiration().substring(0, offer.expiration().indexOf(',')));

    Picasso.with(holder.image.getContext())
        .load(offer.imageUrl())
        .fit()
        .centerCrop()
        .into(holder.image);

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        offerClicks.onNext(offer);
      }
    });
  }

  @Override public int getItemCount() {
    return items.size();
  }

  class VHItem extends RecyclerView.ViewHolder {
    ImageView image;
    TextView name;
    TextView description;
    TextView expiration;

    public VHItem(View itemView) {
      super(itemView);
      image = (ImageView) itemView.findViewById(R.id.image);
      name = (TextView) itemView.findViewById(R.id.name);
      description = (TextView) itemView.findViewById(R.id.description);
      expiration = (TextView) itemView.findViewById(R.id.expiration);
    }
  }
}
