package com.example.hw3_lotfinder;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * LotActivity
 * -----------
 * Displays detailed information about a selected parking lot, including
 * name, pricing, distance, and availability. Also provides buttons for launching
 * external map navigation apps (Google Maps, Waze).
 *
 * The activity supports a custom transition animation when launched from the MapView,
 * and handles graceful UI styling including gradient background and safe system insets.
 */
public class LotActivity extends AppCompatActivity {

    // Flag to determine if this activity was launched from MapView (used for exit animation)
    private boolean fromMap = false;

    /**
     * Lifecycle method triggered when the activity is created.
     * Sets up layout, extracts intent data, and initializes UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable layout to draw behind system bars
        setContentView(R.layout.activity_lot);

        // ----------------------------------
        // Apply Status Bar Insets Padding
        // ----------------------------------
        View statusBarBackground = findViewById(R.id.statusBarBackground);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            statusBarBackground.getLayoutParams().height = systemBars.top;
            statusBarBackground.requestLayout();
            return insets;
        });

        // ----------------------------------
        // Retrieve Passed Data from Intent
        // ----------------------------------
        Bundle bundle = getIntent().getExtras();
        Lot lot = (Lot) bundle.getSerializable("lot"); // Get Lot object
        fromMap = getIntent().getBooleanExtra("fromMap", false); // Get flag to check origin

        // ----------------------------------
        // Initialize and Populate UI Elements
        // ----------------------------------
        TextView tv_distance = findViewById(R.id.tv_distance);
        TextView tv_vacancy = findViewById(R.id.tv_vacancy);
        TextView tv_prices = findViewById(R.id.tv_prices);
        TextView tv_name = findViewById(R.id.tv_name);

        // Set lot values in respective TextViews
        tv_distance.setText(lot.getDistance());
        tv_vacancy.setText(lot.getVacancy());
        tv_prices.setText(lot.getPrices());
        tv_name.setText(lot.getName());

        // ----------------------------------
        // Customize Status Bar Appearance
        // ----------------------------------
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));

        // ----------------------------------
        // Create and Apply Gradient Background
        // ----------------------------------
        int white = getResources().getColor(R.color.white);
        int lightBlue = getResources().getColor(R.color.light_blue);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{white, white, lightBlue} // Transition starts after second white
        );
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setGradientCenter(0.5f, 0.85f); // Gradient starts ~85% down

        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(gradientDrawable);

        // ----------------------------------
        // Button Toast Setup (Placeholders)
        // ----------------------------------
        Button googleMapsButton = findViewById(R.id.googlemaps_button);
        Button wazeButton = findViewById(R.id.waze_button);

        // Placeholder action: shows Toast indicating Maps intent
        googleMapsButton.setOnClickListener(v ->
                Toast.makeText(LotActivity.this, "Will close app and open Google Maps", Toast.LENGTH_LONG).show()
        );

        // Placeholder action: shows Toast indicating Waze intent
        wazeButton.setOnClickListener(v ->
                Toast.makeText(LotActivity.this, "Will close app and open Waze", Toast.LENGTH_LONG).show()
        );
    }

    /**
     * Override the default finish behavior to apply a zoom-out animation
     * if this activity was launched from the MapView.
     */
    @Override
    public void finish() {
        super.finish();
        if (fromMap) {
            overridePendingTransition(0, R.anim.zoom_exit); // Only apply exit animation
        }
    }
}
