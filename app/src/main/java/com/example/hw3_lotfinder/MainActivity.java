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

/**
 * MainActivity
 * ------------
 * Displays a scrollable list of parking lots using a RecyclerView.
 * Receives location and sorting data from LotSearch or MapView.
 * Allows switching to map-based view via toggle switch.
 */
public class MainActivity extends AppCompatActivity {

    // Firebase Analytics instance for tracking user behavior
    private FirebaseAnalytics mFirebaseAnalytics;

    // Coordinates and query received via Intent
    private String lat = "0.0";
    private String lng = "0.0";
    private String query = "";
    private String sortType = "Distance";

    /**
     * Initializes the main activity layout, populates data into RecyclerView,
     * and handles navigation to the map view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // --------------------------------------------
        // Apply system bar insets to avoid UI overlap
        // --------------------------------------------
        View statusBarBackground = findViewById(R.id.statusBarBackground);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            statusBarBackground.getLayoutParams().height = systemBars.top;
            statusBarBackground.requestLayout();
            return insets;
        });

        // --------------------------------------------
        // Retrieve values passed via Intent
        // --------------------------------------------
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getString("lat");
            lng = extras.getString("lng");
            query = extras.getString("query");
            sortType = extras.getString("sort");
        }

        // --------------------------------------------
        // Initialize Firebase Analytics
        // --------------------------------------------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // --------------------------------------------
        // Setup RecyclerView for displaying lots
        // --------------------------------------------
        RecyclerView rv = findViewById(R.id.rv);
        rv.setHasFixedSize(false); // Allows flexible item height

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        rv.setLayoutManager(layoutManager);

        // Set adapter with sort type and coordinates
        LotFinderAdapter adapter = new LotFinderAdapter(sortType, lat, lng);
        rv.setAdapter(adapter);

        // --------------------------------------------
        // Set system status bar color
        // --------------------------------------------
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));

        // --------------------------------------------
        // Create and apply background gradient
        // --------------------------------------------
        int white = getResources().getColor(R.color.white);
        int lightBlue = getResources().getColor(R.color.light_blue);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{white, white, lightBlue} // Fades to light blue from white
        );
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setGradientCenter(0.5f, 0.85f); // Start gradient lower on screen

        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(gradientDrawable);

        // --------------------------------------------
        // Setup view switch toggle for navigating to map
        // --------------------------------------------
        Switch switchView = findViewById(R.id.s_ViewSwitch);

        // If toggled ON, switch to MapView
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent intent = new Intent(MainActivity.this, MapView.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("query", query);
                intent.putExtra("sort", sortType);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // Close this activity to prevent stack build-up
            }
        });
    }
}
