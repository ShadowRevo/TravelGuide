package com.example.fproject1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new android.content.Intent(this, MainActivity.class));
            finish();
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.auth_container, new LoginFragment())
                .commit();
    }

    public void showRegister() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.auth_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }
}