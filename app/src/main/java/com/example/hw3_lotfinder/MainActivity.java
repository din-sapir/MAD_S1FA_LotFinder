package com.example.hw3_lotfinder;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

// Main activity displaying a list of parking lots
public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply system bar insets to avoid UI overlap
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize RecyclerView
        RecyclerView rv = findViewById(R.id.rv);
        rv.setHasFixedSize(false); // Allows dynamic resizing of items

        // Set up RecyclerView layout and adapter
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        rv.setLayoutManager(layoutManager);
        LotFinderAdapter adapter = new LotFinderAdapter();
        rv.setAdapter(adapter);

        // Set the status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));

        // Define gradient colors for background
        int white = getResources().getColor(R.color.white);
        int lightBlue = getResources().getColor(R.color.light_blue);

        // Create a gradient background transitioning from white to light blue
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,  // Gradient direction
                new int[]{white, white, lightBlue}       // Extended white before transition
        );
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setGradientCenter(0.5f, 0.85f);  // Start transition at 85% of screen height

        // Apply the gradient background to the activity
        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(gradientDrawable);

        // Initialize switch for toggling between views
        Switch switchView = findViewById(R.id.s_ViewSwitch);

        // Handle switch toggle to navigate to MapView
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {  // If turned ON, open MapView
                // throw new RuntimeException("Test Crash"); // Force a crash
                Intent intent = new Intent(MainActivity.this, MapView.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Smooth transition
                finish(); // Close MainActivity to prevent returning via the back button
            }
        });
    }
}
