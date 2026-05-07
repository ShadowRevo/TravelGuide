package com.example.fproject1;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlacesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private EditText etTripPrompt;
    private Button btnGenerateTrip;
    private TextView tvItinerary;
    private List<Place> allPlaces = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        recyclerView = view.findViewById(R.id.placesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        etTripPrompt = view.findViewById(R.id.et_trip_prompt);
        btnGenerateTrip = view.findViewById(R.id.btn_generate_trip);
        tvItinerary = view.findViewById(R.id.tv_itinerary);
        String cityName = "";

        if (getArguments() != null) {
            cityName = getArguments().getString("city_name");
        }

        List<Place> places = DataProvider.getPlacesByCity(cityName);
        if (places != null) {
            allPlaces = new ArrayList<>(places);
        }

        if (places != null) {
            adapter = new PlacesAdapter(places);
            recyclerView.setAdapter(adapter);
            etTripPrompt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyAiSearch(s.toString(), false);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
            btnGenerateTrip.setOnClickListener(v ->
                    applyAiSearch(etTripPrompt.getText().toString().trim(), true));
        }
        return view;
    }

    private void applyAiSearch(String rawPrompt, boolean generatePlan) {
        String prompt = rawPrompt == null ? "" : rawPrompt.trim();
        List<Place> rankedPlaces = TextUtils.isEmpty(prompt)
                ? new ArrayList<>(allPlaces)
                : SmartAiHelper.rankPlacesInCityByNaturalLanguage(prompt, allPlaces);

        if (generatePlan) {
            String itineraryPrompt = TextUtils.isEmpty(prompt)
                    ? "best city highlights"
                    : prompt;
            tvItinerary.setText(SmartAiHelper.generateItinerary(rankedPlaces, itineraryPrompt));
        } else if (TextUtils.isEmpty(prompt)) {
            tvItinerary.setText("AI plan will appear here.");
        } else {
            tvItinerary.setText("AI filtered results for: " + prompt);
        }
        adapter = new PlacesAdapter(rankedPlaces);
        recyclerView.setAdapter(adapter);
    }
}