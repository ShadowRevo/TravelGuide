package com.example.fproject1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class ReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviewList;
    private String placeName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        placeName = getArguments() != null ? getArguments().getString("place_name", "") : "";
        recyclerView = view.findViewById(R.id.recycler_reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList, getActivity());
        reviewAdapter.setOnReviewLongClickListener((review, position) -> {
            if (position == RecyclerView.NO_POSITION) return;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserName = (user != null && user.getEmail() != null)
                    ? user.getEmail()
                    : "Anonymous";
            if (!currentUserName.equals(review.getUserName())) {
                Toast.makeText(getContext(), "You can delete only your own review", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete review")
                    .setMessage("Do you want to delete your review?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        ReviewStorage.deleteReview(requireContext(), placeName, review, deleted -> {
                            if (!isAdded()) {
                                return;
                            }
                            if (deleted) {
                                reviewList.remove(position);
                                reviewAdapter.notifyItemRemoved(position);
                                Toast.makeText(getContext(), "Review deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Could not delete the review. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        recyclerView.setAdapter(reviewAdapter);
        ReviewStorage.getReviews(requireContext(), placeName, reviews -> {
            if (!isAdded()) {
                return;
            }
            reviewList.clear();
            reviewList.addAll(reviews);
            reviewAdapter.notifyDataSetChanged();
        });
        view.findViewById(R.id.btn_add_review).setOnClickListener(v -> {
            AddReviewFragment addReviewFragment = new AddReviewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("place_name", placeName);
            addReviewFragment.setArguments(bundle);
            ((MainActivity) requireActivity()).replaceFragment(addReviewFragment, true);
        });
        return view;
    }
}