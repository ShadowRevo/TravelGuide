package com.example.fproject1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startEntranceAnimations();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Intent intent;
            if (mAuth.getCurrentUser() != null) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
    }, 2200);
}

    private void startEntranceAnimations() {
        View panel = findViewById(R.id.panel);
        View logo = findViewById(R.id.logo_container);
        View logoIcon = findViewById(R.id.logo_icon);
        TextView title = findViewById(R.id.tv_app_name);
        TextView subtitle = findViewById(R.id.tv_sub);
        TextView powered = findViewById(R.id.tv_powered);
        View orbTop = findViewById(R.id.orb_top);
        View orbBottom = findViewById(R.id.orb_bottom);

        panel.setAlpha(0f);
        panel.setTranslationY(80f);
        title.setAlpha(0f);
        subtitle.setAlpha(0f);
        subtitle.setTranslationY(18f);
        powered.setAlpha(0f);

        ObjectAnimator panelFade = ObjectAnimator.ofFloat(panel, View.ALPHA, 0f, 1f);
        panelFade.setDuration(650);
        panelFade.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator panelRise = ObjectAnimator.ofFloat(panel, View.TRANSLATION_Y, 80f, 0f);
        panelRise.setDuration(700);
        panelRise.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator titleFade = ObjectAnimator.ofFloat(title, View.ALPHA, 0f, 1f);
        titleFade.setDuration(500);
        titleFade.setStartDelay(280);

        ObjectAnimator subtitleFade = ObjectAnimator.ofFloat(subtitle, View.ALPHA, 0f, 1f);
        subtitleFade.setDuration(500);
        subtitleFade.setStartDelay(420);

        ObjectAnimator subtitleRise = ObjectAnimator.ofFloat(subtitle, View.TRANSLATION_Y, 18f, 0f);
        subtitleRise.setDuration(500);
        subtitleRise.setStartDelay(420);

        ObjectAnimator poweredFade = ObjectAnimator.ofFloat(powered, View.ALPHA, 0f, 0.82f);
        poweredFade.setDuration(700);
        poweredFade.setStartDelay(620);

        AnimatorSet introSet = new AnimatorSet();
        introSet.playTogether(panelFade, panelRise, titleFade, subtitleFade, subtitleRise, poweredFade);
        introSet.start();

        ObjectAnimator logoPulseX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 1f, 1.07f, 1f);
        logoPulseX.setDuration(1700);
        logoPulseX.setRepeatCount(ObjectAnimator.INFINITE);

        ObjectAnimator logoPulseY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 1f, 1.07f, 1f);
        logoPulseY.setDuration(1700);
        logoPulseY.setRepeatCount(ObjectAnimator.INFINITE);

        ObjectAnimator iconFloat = ObjectAnimator.ofFloat(logoIcon, View.ROTATION, -8f, 8f, -8f);
        iconFloat.setDuration(2600);
        iconFloat.setInterpolator(new AccelerateDecelerateInterpolator());
        iconFloat.setRepeatCount(ObjectAnimator.INFINITE);

        ObjectAnimator orbTopDrift = ObjectAnimator.ofFloat(orbTop, View.TRANSLATION_Y, 0f, 18f, 0f);
        orbTopDrift.setDuration(3800);
        orbTopDrift.setInterpolator(new AccelerateDecelerateInterpolator());
        orbTopDrift.setRepeatCount(ObjectAnimator.INFINITE);

        ObjectAnimator orbBottomDrift = ObjectAnimator.ofFloat(orbBottom, View.TRANSLATION_Y, 0f, -20f, 0f);
        orbBottomDrift.setDuration(4300);
        orbBottomDrift.setInterpolator(new AccelerateDecelerateInterpolator());
        orbBottomDrift.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet loopSet = new AnimatorSet();
        loopSet.playTogether(logoPulseX, logoPulseY, iconFloat, orbTopDrift, orbBottomDrift);
        loopSet.start();
    }
}