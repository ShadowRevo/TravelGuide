package com.example.fproject1;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class ReviewStorage {

    private static final String COLLECTION_REVIEWS = "reviews";
    private static final String COLLECTION_ENTRIES = "entries";
    private static final String FIELD_USER_NAME = "userName";
    private static final String FIELD_TEXT = "text";

    public interface ReviewsCallback {
        void onResult(ArrayList<Review> reviews);
    }

    public interface SaveCallback {
        void onComplete(boolean success);
    }

    public interface DeleteCallback {
        void onComplete(boolean deleted);
    }

    private ReviewStorage() {}

    public static void getReviews(Context context, String placeName, @NonNull ReviewsCallback callback) {
        String placeKey = getPlaceKey(placeName);
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_REVIEWS)
                .document(placeKey)
                .collection(COLLECTION_ENTRIES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Review> reviews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Review review = new Review(
                                document.getString(FIELD_USER_NAME) == null ? "Anonymous" : document.getString(FIELD_USER_NAME),
                                document.getString(FIELD_TEXT) == null ? "" : document.getString(FIELD_TEXT)
                        );
                        review.setId(document.getId());
                        reviews.add(review);
                    }
                    callback.onResult(reviews);
                })
                .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
    }

    public static void addReview(Context context, String placeName, Review review, @Nullable SaveCallback callback) {
        String placeKey = getPlaceKey(placeName);
        Map<String, Object> payload = new HashMap<>();
        payload.put(FIELD_USER_NAME, review.getUserName() == null ? "Anonymous" : review.getUserName());
        payload.put(FIELD_TEXT, review.getText() == null ? "" : review.getText());
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_REVIEWS)
                .document(placeKey)
                .collection(COLLECTION_ENTRIES)
                .add(payload)
                .addOnSuccessListener(documentReference -> {
                    review.setId(documentReference.getId());
                    if (callback != null) callback.onComplete(true);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onComplete(false);
                });
    }

    public static void deleteReview(Context context, String placeName, Review reviewToDelete, @NonNull DeleteCallback callback) {
        if (reviewToDelete == null) {
            callback.onComplete(false);
            return;
        }
        String placeKey = getPlaceKey(placeName);
        String reviewId = reviewToDelete.getId();

        if (!TextUtils.isEmpty(reviewId)) {
            FirebaseFirestore.getInstance()
                    .collection(COLLECTION_REVIEWS)
                    .document(placeKey)
                    .collection(COLLECTION_ENTRIES)
                    .document(reviewId)
                    .delete()
                    .addOnSuccessListener(unused -> callback.onComplete(true))
                    .addOnFailureListener(e -> callback.onComplete(false));
            return;
        }

        String userName = reviewToDelete.getUserName() == null ? "Anonymous" : reviewToDelete.getUserName();
        String text = reviewToDelete.getText() == null ? "" : reviewToDelete.getText();
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_REVIEWS)
                .document(placeKey)
                .collection(COLLECTION_ENTRIES)
                .whereEqualTo(FIELD_USER_NAME, userName)
                .whereEqualTo(FIELD_TEXT, text)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onComplete(false);
                        return;
                    }
                    queryDocumentSnapshots.getDocuments().get(0).getReference().delete()
                            .addOnSuccessListener(unused -> callback.onComplete(true))
                            .addOnFailureListener(e -> callback.onComplete(false));
                })
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    private static String getPlaceKey(String placeName) {
        return (placeName == null ? "" : placeName.trim().toLowerCase(Locale.ROOT));
    }
}