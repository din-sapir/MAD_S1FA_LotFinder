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

public class StartScreen extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> signInLauncher;

    private static final int LOGO_START_DELAY = 200;
    private static final int SLIDE_IN_DURATION = 800;
    private static final int BACKGROUND_FADE_DELAY = LOGO_START_DELAY + SLIDE_IN_DURATION;

    private ImageView logo;
    private CardView signInCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_screen);

        View mainLayout = findViewById(R.id.main);
        logo = findViewById(R.id.imageView3);
        signInCard = findViewById(R.id.cv_signin);
        signInCard.setVisibility(View.INVISIBLE); // start hidden

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail()
                .requestIdToken("1062718784584-0440s078f5altrphr8rp698mg8lnkjdj.apps.googleusercontent.com")
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

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

        Button loginBtn = findViewById(R.id.btn_googlesignin);
        loginBtn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });

        // 1. Delay logo slide-in animation by 200ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            logo.setVisibility(View.VISIBLE);
            logo.startAnimation(slideIn);
        }, LOGO_START_DELAY);

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

        // 3. Show sign-in card
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            signInCard.setVisibility(View.VISIBLE);
            signInCard.setAlpha(0f);
            signInCard.animate().alpha(1f).setDuration(500).start();
        }, BACKGROUND_FADE_DELAY);
    }

    private void handleSignInData(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(this, "Welcome " + account.getDisplayName(), Toast.LENGTH_LONG).show();

            // Hide sign-in card
            signInCard.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                signInCard.setVisibility(View.GONE);

                // Slide logo out
                Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
                logo.startAnimation(slideOut);

                // Move to next screen after slide out
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(StartScreen.this, LotSearch.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }, 700); // matches slide out duration
            }).start();

        } catch (ApiException e) {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }
}
