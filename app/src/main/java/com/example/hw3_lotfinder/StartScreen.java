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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * StartScreen Activity
 * --------------------
 * Landing screen that handles Google Sign-In authentication with
 * introductory animations for logo and background.
 */
public class StartScreen extends AppCompatActivity {

    // Google Sign-In related
    GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> signInLauncher;

    // Animation timing constants
    private static final int LOGO_START_DELAY = 200;
    private static final int SLIDE_IN_DURATION = 800;
    private static final int BACKGROUND_FADE_DELAY = LOGO_START_DELAY + SLIDE_IN_DURATION;

    private ImageView logo;
    private CardView signInCard;

    /**
     * Initializes UI, sets up Google Sign-In, and plays intro animations.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_screen);

        View mainLayout = findViewById(R.id.main);
        logo = findViewById(R.id.imageView3);                 // Logo image
        signInCard = findViewById(R.id.cv_signin);            // Sign-in card container
        signInCard.setVisibility(View.INVISIBLE);             // Hide sign-in card initially

        // Apply system window insets (for immersive display)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // -----------------------------
        // Google Sign-In Configuration
        // -----------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail()
                .requestIdToken("1062718784584-0440s078f5altrphr8rp698mg8lnkjdj.apps.googleusercontent.com")
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        // Handle result from Google Sign-In intent
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> task =
                                GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInData(task);
                    }
                }
        );

        // Set listener for login button
        Button loginBtn = findViewById(R.id.btn_googlesignin);
        loginBtn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });

        // -----------------------------
        // Animation Sequence
        // -----------------------------

        // 1. Slide logo in from top after short delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            logo.setVisibility(View.VISIBLE);
            logo.startAnimation(slideIn);
        }, LOGO_START_DELAY);

        // 2. Fade background from blue to white after logo finishes sliding in
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ObjectAnimator colorAnim = ObjectAnimator.ofObject(
                    mainLayout,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    Color.parseColor("#055E8E"), // initial color
                    Color.WHITE                 // target color
            );
            colorAnim.setDuration(600);
            colorAnim.start();
        }, BACKGROUND_FADE_DELAY);

        // 3. Fade in sign-in card after background transition
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            signInCard.setVisibility(View.VISIBLE);
            signInCard.setAlpha(0f);
            signInCard.animate().alpha(1f).setDuration(500).start();
        }, BACKGROUND_FADE_DELAY);
    }

    /**
     * Handles Google Sign-In result and transitions to the next screen.
     * @param task Task containing GoogleSignInAccount or failure
     */
    private void handleSignInData(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            // Greet the user
            Toast.makeText(this, "Welcome " + account.getDisplayName(), Toast.LENGTH_LONG).show();

            // Animate sign-in card fade out
            signInCard.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                signInCard.setVisibility(View.GONE);

                // Animate logo slide out
                Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
                logo.startAnimation(slideOut);

                // Launch LotSearch activity after animation ends
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(StartScreen.this, LotSearch.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish(); // Close StartScreen activity
                }, 700); // match slide out animation duration

            }).start();

        } catch (ApiException e) {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }
}
