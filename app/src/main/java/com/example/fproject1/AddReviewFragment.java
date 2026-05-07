package com.example.fproject1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddReviewFragment extends Fragment {

    private EditText etReview;
    private Button btnSubmit;
    private String placeName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_review, container, false);
        placeName = getArguments() != null ? getArguments().getString("place_name", "") : "";
        etReview = view.findViewById(R.id.et_review);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> submitReview());
        return view;
    }

    private void submitReview() {
        String text = etReview.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            etReview.setError("Please write a review");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userName = "Anonymous";
        if (user != null && user.getEmail() != null) userName = user.getEmail();
        btnSubmit.setEnabled(false);

        ReviewStorage.addReview(requireContext(), placeName, new Review(userName, text), success -> {
            btnSubmit.setEnabled(true);
            if (!isAdded())
                return;

            if (success) {
                Toast.makeText(getActivity(), "Review submitted successfully.", Toast.LENGTH_SHORT).show();
                etReview.setText("");
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getActivity(), "Could not submit your review. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}