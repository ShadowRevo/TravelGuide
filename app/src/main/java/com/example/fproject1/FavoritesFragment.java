package com.example.fproject1;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private LinearLayout favContainer;
    private TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        favContainer = view.findViewById(R.id.fav_container);
        emptyView = view.findViewById(R.id.tv_empty);
        loadFavorites();
        return view;
    }

    private void loadFavorites() {
        FavoriteStorage.getFavorites(favSet -> {
            if (!isAdded()) return;
            favContainer.removeAllViews();
            if (favSet.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                return;
            }
            emptyView.setVisibility(View.GONE);
            for (String name : favSet) {
                Place place = findPlace(name);
                if (place == null) continue;
                View item = LayoutInflater.from(getContext())
                        .inflate(R.layout.place_item, favContainer, false);
                TextView tvName = item.findViewById(R.id.tv_place_name);
                ImageView img = item.findViewById(R.id.img_place);
                ImageView favIcon = item.findViewById(R.id.btn_fav_icon);
                tvName.setText(place.getName());
                img.setImageResource(place.getImage());
                favIcon.setImageResource(R.drawable.fav_icon);
                favIcon.setOnClickListener(v -> FavoriteStorage.toggleFavorite(place.getName(), (success, isFavoriteNow) -> {
                    if (success && !isFavoriteNow) {
                        loadFavorites();
                    }
                }));
                item.setOnClickListener(v -> openPlaceDetails(place));
                favContainer.addView(item);
            }
        });
    }

    private void openPlaceDetails(Place place) {
        Bundle bundle = new Bundle();
        bundle.putString("name", place.getName());
        bundle.putString("desc", place.getDescription());
        bundle.putString("hours", place.getHours());
        bundle.putString("fee", place.getFee());
        bundle.putString("address", place.getAddress());
        bundle.putInt("image", place.getImage());
        PlaceDetailsFragment fragment = new PlaceDetailsFragment();
        fragment.setArguments(bundle);
        ((MainActivity) requireActivity()).replaceFragment(fragment, true);
    }

    private Place findPlace(String name) {
        for (List<Place> list : DataProvider.getAllData().values()) {
            for (Place p : list) {
                if (p.getName().equals(name)) {
                    return p;
                }
            }
        }
        return null;
    }
}