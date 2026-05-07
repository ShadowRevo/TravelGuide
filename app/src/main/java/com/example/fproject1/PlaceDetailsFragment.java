package com.example.fproject1;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.Locale;

public class PlaceDetailsFragment extends Fragment implements TextToSpeech.OnInitListener {
    private TextView tvName, tvDesc, tvHours, tvFee, tvAddress;
    private ImageView imgPlace;
    private Button btnFav, btnMap, btnTranslate, btnReadDescription, btnViewReviews, btnAddReview;
    private Spinner spinnerLanguage;
    private TextToSpeech textToSpeech;
    private boolean textToSpeechReady;
    private String name, desc, hours, fee, address;
    private int image;

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_details, container, false);
        tvName = view.findViewById(R.id.tv_place_name);
        tvDesc = view.findViewById(R.id.tv_description);
        tvHours = view.findViewById(R.id.tv_hours);
        tvFee = view.findViewById(R.id.tv_fee);
        tvAddress = view.findViewById(R.id.tv_address);
        imgPlace = view.findViewById(R.id.img_place);
        btnFav = view.findViewById(R.id.btn_fav);
        btnMap = view.findViewById(R.id.btn_map);
        btnTranslate = view.findViewById(R.id.btn_translate);
        btnReadDescription = view.findViewById(R.id.btn_read_description);
        btnViewReviews = view.findViewById(R.id.btn_view_reviews);
        btnAddReview = view.findViewById(R.id.btn_add_review);
        spinnerLanguage = view.findViewById(R.id.spinner_language);
        textToSpeech = new TextToSpeech(requireContext(), this);

        String[] languages = {"English", "Spanish", "French", "Arabic", "Hebrew"};
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                languages
        );
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(langAdapter);
        if (getArguments() != null) {
            name = getArguments().getString("name", "");
            desc = getArguments().getString("desc", "");
            hours = getArguments().getString("hours", "");
            fee = getArguments().getString("fee", "");
            address = getArguments().getString("address", "");
            image = getArguments().getInt("image", 0);
        }
        if (TextUtils.isEmpty(desc)) {
            desc = getString(R.string.general_description_fallback);
        }

        tvName.setText(name);
        tvDesc.setText(desc);
        tvHours.setText("Hours: " + hours);
        tvFee.setText("Fee: " + fee);
        tvAddress.setText("Address: " + address);

        if (image != 0) {
            imgPlace.setImageResource(image);
        }

        // Check permission and schedule notification reminder after 5 seconds
        checkPermissionAndSchedule();

        FavoriteStorage.isFavorite(name, this::updateButton);
        btnFav.setOnClickListener(v -> FavoriteStorage.toggleFavorite(name, (success, isFavoriteNow) -> {
            if (!isAdded())
                return;

            if (!success) {
                Toast.makeText(getContext(), "Could not update favorites. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), isFavoriteNow ? "Added to favorites." : "Removed from favorites.", Toast.LENGTH_SHORT).show();
            updateButton(isFavoriteNow);
        }));

        btnTranslate.setOnClickListener(v -> {
            String selectedLang = spinnerLanguage.getSelectedItem().toString();
            tvDesc.setText(SmartAiHelper.translateForTourist(desc, selectedLang));
            tvHours.setText(SmartAiHelper.translateForTourist("Hours: " + hours, selectedLang));
            tvFee.setText(SmartAiHelper.translateForTourist("Fee: " + fee, selectedLang));
            tvAddress.setText(SmartAiHelper.translateForTourist("Address: " + address, selectedLang));
        });

        btnReadDescription.setOnClickListener(v -> readDescriptionAloud());

        btnMap.setOnClickListener(v -> {
            String mapQuery = address == null || address.trim().isEmpty() ? name : address;
            android.net.Uri directionsUri = android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                    + android.net.Uri.encode(mapQuery)
                    + "&travelmode=driving");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, directionsUri);
            startActivity(mapIntent);
        });

        btnViewReviews.setOnClickListener(v -> {
            ReviewsFragment fragment = new ReviewsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("place_name", name);
            fragment.setArguments(bundle);
            ((MainActivity) requireActivity()).replaceFragment(fragment, true);
        });

        btnAddReview.setOnClickListener(v -> {
            AddReviewFragment fragment = new AddReviewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("place_name", name);
            fragment.setArguments(bundle);
            ((MainActivity) requireActivity()).replaceFragment(fragment, true);
        });

        return view;
    }

    private void checkPermissionAndSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            } else {
                scheduleReminder();
            }
        } else {
            scheduleReminder();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scheduleReminder();
            } else {
                Toast.makeText(getContext(), "Notification permission denied. Reminder won't be shown.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scheduleReminder() {
        if (getContext() == null) return;

        Log.d("PlaceDetailsFragment", "Scheduling reminder for " + name + " in 5 seconds");

        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_PLACE_NAME, name);
        intent.putExtra(ReminderReceiver.EXTRA_PLACE_FEE, fee);

        int requestCode = Math.abs((name == null ? "place" : name).hashCode());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            long triggerTime = System.currentTimeMillis() + 5000; // 5 seconds delay
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int languageResult = textToSpeech.setLanguage(Locale.US);
            textToSpeechReady = languageResult != TextToSpeech.LANG_MISSING_DATA
                    && languageResult != TextToSpeech.LANG_NOT_SUPPORTED;
        }
    }

    @Override
    public void onDestroyView() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroyView();
    }

    private void readDescriptionAloud() {
        if (!textToSpeechReady) {
            Toast.makeText(getContext(), "Text to speech is not ready yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = tvDesc.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(getContext(), "There is no description to read.", Toast.LENGTH_SHORT).show();
            return;
        }

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "place_description");
    }

    private void updateButton(boolean isFav) {
        btnFav.setText(isFav ? "Remove Favorite" : "Add Favorite");
    }
}