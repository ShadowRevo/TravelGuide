package com.example.fproject1;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CityAdapter adapter;
    private List<City> cities;
    private EditText searchBar;
    private ImageView profileImage;
    private TextView tvWelcomeUser;
    private Button btnFavorites;
    private Button btnPlanAssistant;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.citiesRecycler);
        searchBar = view.findViewById(R.id.searchBar);
        profileImage = view.findViewById(R.id.profileImage);
        tvWelcomeUser = view.findViewById(R.id.tv_welcome_user);
        btnFavorites = view.findViewById(R.id.btn_favorites);
        btnPlanAssistant = view.findViewById(R.id.btn_plan_assistant);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cities = new ArrayList<>();
        cities.add(new City("Tel Aviv", R.drawable.tel_aviv));
        cities.add(new City("Jerusalem", R.drawable.jerusalem));
        cities.add(new City("Haifa", R.drawable.haifa));
        cities.add(new City("Eilat", R.drawable.eilat));
        cities.add(new City("Akko", R.drawable.akko));
        cities.add(new City("Nazareth", R.drawable.nazareth));
        cities.add(new City("Tiberias", R.drawable.tiberias));
        adapter = new CityAdapter(cities);
        recyclerView.setAdapter(adapter);
        loadWelcomeMessage();
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<City> filtered = SmartAiHelper.rankCitiesByNaturalLanguage(
                        s == null ? "" : s.toString(),
                        cities
                );
                adapter.updateList(filtered);
            }
        });

        profileImage.setOnClickListener(v ->
                    ((MainActivity) requireActivity()).replaceFragment(new ProfileFragment(), true)
                    );

        btnFavorites.setOnClickListener(v ->
                    ((MainActivity) requireActivity()).replaceFragment(new FavoritesFragment(), true)
                    );

        btnPlanAssistant.setOnClickListener(v -> showPlanDialog());
        return view;
        }

    private void loadWelcomeMessage() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            tvWelcomeUser.setText("Welcome!");
            return;
        }

        firestore.collection("users")
                .document(user.getUid())
                .get(Source.SERVER)
                .addOnSuccessListener(document -> {
                    String username = document.getString("username");
                    if (username == null || username.trim().isEmpty()) {
                        tvWelcomeUser.setText("Welcome!");
                    } else {
                        tvWelcomeUser.setText("Welcome, " + username + " 👋");
                    }
                })
                .addOnFailureListener(e -> tvWelcomeUser.setText("Welcome!"));
    }

    private void showPlanDialog() {
        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);
        EditText daysInput = new EditText(requireContext());
        daysInput.setHint("Number of days");
        daysInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        container.addView(daysInput);
        TextView planTypeTitle = new TextView(requireContext());
        planTypeTitle.setText("Plan type");
        planTypeTitle.setPadding(0, padding, 0, 0);
        container.addView(planTypeTitle);
        RadioGroup group = new RadioGroup(requireContext());
        group.setOrientation(RadioGroup.VERTICAL);
        RadioButton freePlan = new RadioButton(requireContext());
        freePlan.setText("Free plan");
        freePlan.setId(View.generateViewId());
        freePlan.setChecked(true);
        RadioButton feePlan = new RadioButton(requireContext());
        feePlan.setText("Fee plan");
        feePlan.setId(View.generateViewId());
        group.addView(freePlan);
        group.addView(feePlan);
        container.addView(group);
        TextView cityTitle = new TextView(requireContext());
        cityTitle.setText("Cities");
        cityTitle.setPadding(0, padding, 0, 0);
        container.addView(cityTitle);
        final List<String> allCities = new ArrayList<>(DataProvider.getAllData().keySet());
        Collections.sort(allCities);
        final List<String> selectedCities = new ArrayList<>(allCities);
        Button chooseCitiesButton = new Button(requireContext());
        chooseCitiesButton.setText("Choose cities");
        container.addView(chooseCitiesButton);
        TextView selectedCitiesPreview = new TextView(requireContext());
        selectedCitiesPreview.setText(buildSelectedCitiesText(selectedCities));
        container.addView(selectedCitiesPreview);
        chooseCitiesButton.setOnClickListener(v -> showCityChooser(allCities, selectedCities, selectedCitiesPreview));

        new AlertDialog.Builder(requireContext())
                .setTitle("AI Multi-City Planner")
                .setView(container)
                .setPositiveButton("Generate", (dialog, which) -> {
                    String daysText = daysInput.getText().toString().trim();
                    if (daysText.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter the number of days for your trip.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int days = Integer.parseInt(daysText);
                    if (days <= 0) {
                        Toast.makeText(getContext(), "Please enter at least 1 day for your trip.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedCities.isEmpty()) {
                        Toast.makeText(getContext(), "Please choose at least one city", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean isFreePlan = group.getCheckedRadioButtonId() == freePlan.getId();
                    String plan = generateMultiCityPlan(days, isFreePlan, selectedCities);

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Your " + days + "-Day Plan")
                            .setMessage(plan)
                            .setPositiveButton("OK", null)
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCityChooser(List<String> allCities, List<String> selectedCities, TextView selectedCitiesPreview) {
        String[] cityItems = allCities.toArray(new String[0]);
        boolean[] checkedItems = new boolean[cityItems.length];

        for (int i = 0; i < cityItems.length; i++) {
            checkedItems[i] = selectedCities.contains(cityItems[i]);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Select cities")
                .setMultiChoiceItems(cityItems, checkedItems, (dialog, which, isChecked) -> {
                    String city = cityItems[which];
                    if (isChecked) {
                        if (!selectedCities.contains(city)) {
                            selectedCities.add(city);
                        }
                    } else {
                        selectedCities.remove(city);
                    }
                })
                .setPositiveButton("Done", (dialog, which) -> selectedCitiesPreview.setText(buildSelectedCitiesText(selectedCities)))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String buildSelectedCitiesText(List<String> selectedCities) {
        if (selectedCities.isEmpty()) {
            return "No cities selected";
        }
        StringBuilder sb = new StringBuilder("Selected: ");
        for (int i = 0; i < selectedCities.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(selectedCities.get(i));
        }
        return sb.toString();
    }

    private String generateMultiCityPlan(int days, boolean freePlan, List<String> chosenCities) {
        StringBuilder sb = new StringBuilder();
        List<String> cityPool = new ArrayList<>(chosenCities);

        if (cityPool.isEmpty()) {
            return "No cities available right now.";
        }

        Collections.shuffle(cityPool);

        for (int day = 1; day <= days; day++) {
            if ((day - 1) % cityPool.size() == 0) {
                Collections.shuffle(cityPool);
            }

            String city = cityPool.get((day - 1) % cityPool.size());
            List<Place> places = DataProvider.getPlacesByCity(city);

            Place morning = pickPlace(places, freePlan, true, null);
            Place night = pickPlace(places, freePlan, false, morning);

            sb.append("Day ").append(day).append(" - ").append(city).append("\n");
            sb.append("Morning: ").append(formatActivity(morning, places, freePlan)).append("\n");
            sb.append("Night: ").append(formatActivity(night, places, freePlan)).append("\n\n");
        }

        return sb.toString().trim();
    }

    private Place pickPlace(List<Place> places, boolean freePlan, boolean morning, Place excludePlace) {
        if (places == null || places.isEmpty()) return null;

        List<Place> filtered = new ArrayList<>();
        List<Place> preferred = new ArrayList<>();

        for (Place p : places) {
            if (excludePlace != null && p.getName().equalsIgnoreCase(excludePlace.getName())) {
                continue;
            }

            if (freePlan && !"free".equalsIgnoreCase(p.getFee())) {
                continue;
            }

            filtered.add(p);

            if (morning && !p.getHours().contains("24/7")) {
                preferred.add(p);
            }

            if (!morning && isNightFriendly(p)) {
                preferred.add(p);
            }
        }

        if (!preferred.isEmpty()) {
            return preferred.get(ThreadLocalRandom.current().nextInt(preferred.size()));
        }

        if (!filtered.isEmpty()) {
            return filtered.get(ThreadLocalRandom.current().nextInt(filtered.size()));
        }

        return null;
    }

    private boolean isNightFriendly(Place place) {
        String name = place.getName().toLowerCase(Locale.ROOT);
        String description = place.getDescription().toLowerCase(Locale.ROOT);
        return place.getHours().contains("24/7")
                || name.contains("market")
                || name.contains("port")
                || name.contains("promenade")
                || name.contains("marina")
                || description.contains("nightlife");
    }

    private String formatActivity(Place selected, List<Place> allPlaces, boolean freePlan) {
        if (selected != null) {
            return selected.getName() + " (" + selected.getFee() + ")";
        }

        if (freePlan) {
            return "No free activity found in this city. Consider switching to the fee plan.";
        }

        if (allPlaces == null || allPlaces.isEmpty()) {
            return "No activity available.";
        }

        Place fallback = allPlaces.get(0);
        return fallback.getName() + " (" + fallback.getFee() + ")";
    }
}