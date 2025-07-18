package com.example.hw3_lotfinder;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartScreen extends AppCompatActivity {

    private static final int LOGO_START_DELAY = 200;
    private static final int SLIDE_IN_DURATION = 800;
    private static final int BACKGROUND_FADE_DELAY = LOGO_START_DELAY + SLIDE_IN_DURATION;
    private static final int SLIDE_OUT_DELAY = BACKGROUND_FADE_DELAY + 300;
    private static final int SPLASH_DURATION = SLIDE_OUT_DELAY + 720;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_screen);

        View mainLayout = findViewById(R.id.main);
        ImageView logo = findViewById(R.id.imageView3);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Delay logo slide-in animation by 200ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            logo.setVisibility(View.VISIBLE);
            logo.startAnimation(slideIn);
        }, 200);

        // 2. Animate background to white after slide-in finishes
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ObjectAnimator colorAnim = ObjectAnimator.ofObject(
                    mainLayout,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    Color.parseColor("#055E8E"),
                    Color.WHITE
            );
            colorAnim.setDuration(600);
            colorAnim.start();
        }, BACKGROUND_FADE_DELAY);

        // 3. Slide logo out of the screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
            logo.startAnimation(slideOut);
        }, SLIDE_OUT_DELAY);

        // 4. Switch to next screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(StartScreen.this, LotSearch.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}
