package com.example.fproject1;
import android.os.Bundle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private final List<Place> list;
    public PlacesAdapter(List<Place> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = list.get(position);
        holder.name.setText(place.getName());
        holder.image.setImageResource(place.getImage());
        holder.favIcon.setImageResource(R.drawable.empty_fav_icon);
        FavoriteStorage.isFavorite(place.getName(), isFav -> holder.favIcon.setImageResource(
                isFav ? R.drawable.fav_icon : R.drawable.empty_fav_icon
        ));
        holder.favIcon.setOnClickListener(v -> FavoriteStorage.toggleFavorite(place.getName(), (success, isFavoriteNow) -> {
            if (success) {
                holder.favIcon.setImageResource(
                        isFavoriteNow ? R.drawable.fav_icon : R.drawable.empty_fav_icon
                );
            }
        }));

        View.OnClickListener openDetailsClick = v -> {
            Bundle bundle = new Bundle();
            bundle.putString("name", place.getName());
            bundle.putString("desc", place.getDescription());
            bundle.putString("hours", place.getHours());
            bundle.putString("fee", place.getFee());
            bundle.putString("address", place.getAddress());
            bundle.putInt("image", place.getImage());
            PlaceDetailsFragment fragment = new PlaceDetailsFragment();
            fragment.setArguments(bundle);
            Context context = v.getContext();
            if (context instanceof MainActivity) {
                ((MainActivity) context).replaceFragment(fragment, true);
            }
        };
        holder.itemView.setOnClickListener(openDetailsClick);
        holder.image.setOnClickListener(openDetailsClick);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image, favIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_place_name);
            image = itemView.findViewById(R.id.img_place);
            favIcon = itemView.findViewById(R.id.btn_fav_icon);
        }
    }
}