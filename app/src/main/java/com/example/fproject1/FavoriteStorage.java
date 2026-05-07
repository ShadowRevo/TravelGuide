package com.example.fproject1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class FavoriteStorage {

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_FAVORITES = "favorites";
    private static final String FIELD_PLACE_NAME = "placeName";
    public interface FavoritesCallback {
        void onResult(Set<String> favorites);
    }
    public interface FavoriteStateCallback {
        void onResult(boolean isFavorite);
    }
    public interface ToggleCallback {
        void onComplete(boolean success, boolean isFavoriteNow);
    }
    private FavoriteStorage() {
    }
    public static void getFavorites(@NonNull FavoritesCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onResult(new HashSet<>());
            return;
        }
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(user.getUid())
                .collection(COLLECTION_FAVORITES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Set<String> favorites = new HashSet<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String placeName = document.getString(FIELD_PLACE_NAME);
                        if (placeName == null || placeName.trim().isEmpty()) {
                            placeName = document.getId();
                        }
                        favorites.add(placeName);
                    }
                    callback.onResult(favorites);
                })
                .addOnFailureListener(e -> callback.onResult(new HashSet<>()));
    }

    public static void isFavorite(@Nullable String placeName, @NonNull FavoriteStateCallback callback) {
        if (placeName == null || placeName.trim().isEmpty()) {
            callback.onResult(false);
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onResult(false);
            return;
        }

        String placeKey = getPlaceKey(placeName);
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(user.getUid())
                .collection(COLLECTION_FAVORITES)
                .document(placeKey)
                .get()
                .addOnSuccessListener(documentSnapshot -> callback.onResult(documentSnapshot.exists()))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public static void toggleFavorite(@Nullable String placeName, @NonNull ToggleCallback callback) {
        if (placeName == null || placeName.trim().isEmpty()) {
            callback.onComplete(false, false);
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onComplete(false, false);
            return;
        }

        String placeKey = getPlaceKey(placeName);
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .document(user.getUid())
                .collection(COLLECTION_FAVORITES)
                .document(placeKey)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        documentSnapshot.getReference()
                                .delete()
                                .addOnSuccessListener(unused -> callback.onComplete(true, false))
                                .addOnFailureListener(e -> callback.onComplete(false, true));
                    } else {
                        FavoriteEntity entity = new FavoriteEntity(placeName);
                        documentSnapshot.getReference()
                                .set(entity)
                                .addOnSuccessListener(unused -> callback.onComplete(true, true))
                                .addOnFailureListener(e -> callback.onComplete(false, false));
                    }
                })
                .addOnFailureListener(e -> callback.onComplete(false, false));
    }

    private static String getPlaceKey(String placeName) {
        return placeName.trim().toLowerCase(Locale.ROOT);
    }

    private static class FavoriteEntity {
        private String placeName;
        @SuppressWarnings("unused")
        public FavoriteEntity() {
        }

        FavoriteEntity(String placeName) {
            this.placeName = placeName;
        }

        @SuppressWarnings("unused")
        public String getPlaceName() {
            return placeName;
        }

        @SuppressWarnings("unused")
        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }
    }
}