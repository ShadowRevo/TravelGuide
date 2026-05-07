package com.example.fproject1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private Button btnLogout, btnChangeUsername, btnChangePassword, btnDeleteAccount;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnChangeUsername = view.findViewById(R.id.btn_change_username);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnDeleteAccount = view.findViewById(R.id.btn_delete_account);
        loadUserData();

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        btnChangeUsername.setOnClickListener(v -> showChangeUsernameDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnDeleteAccount.setOnClickListener(v -> deleteAccount());
        return view;
    }

    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();
        firestore.collection("users")
                .document(uid)
                .get(Source.SERVER)
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvUsername.setText(document.getString("username"));
                        tvEmail.setText(document.getString("email"));
                    } else {
                        Toast.makeText(getContext(), "Profile data was not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Could not load profile details. Please try again.", Toast.LENGTH_SHORT).show()
                );
    }

    private void showChangeUsernameDialog() {
        EditText input = new EditText(getContext());
        input.setHint("New username");
        new AlertDialog.Builder(getContext())
                .setTitle("Change Username")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newUsername = input.getText().toString().trim();
                    if (newUsername.isEmpty()) return;
                    String uid = auth.getCurrentUser().getUid();
                    firestore.collection("users")
                            .document(uid)
                            .update("username", newUsername)
                            .addOnSuccessListener(unused -> {
                                tvUsername.setText(newUsername);
                                Toast.makeText(getContext(), "Username updated successfully.", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        EditText input = new EditText(getContext());
        input.setHint("New password");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(getContext())
                .setTitle("Change Password")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newPass = input.getText().toString().trim();
                    if (newPass.length() < 6) {
                        Toast.makeText(getContext(), "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) return;
                    user.updatePassword(newPass)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(getContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Password update failed. Please log in again and retry.", Toast.LENGTH_LONG).show()
                            );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    firestore.collection("users")
                            .document(uid)
                            .delete();
                    user.delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Your account has been deleted.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AuthActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}