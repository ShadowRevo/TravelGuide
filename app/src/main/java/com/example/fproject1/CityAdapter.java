package com.example.fproject1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<City> list;
    public CityAdapter(List<City> list) {
        this.list = list;
    }
    public void updateList(List<City> newList) {
        list = newList;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        City city = list.get(position);
        holder.name.setText(city.getName());
        holder.image.setImageResource(city.getImage());
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("city_name", city.getName());
            PlacesFragment fragment = new PlacesFragment();
            fragment.setArguments(bundle);
            ((MainActivity) v.getContext())
                    .replaceFragment(fragment, true);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_city_name);
            image = itemView.findViewById(R.id.img_city);
        }
    }
}