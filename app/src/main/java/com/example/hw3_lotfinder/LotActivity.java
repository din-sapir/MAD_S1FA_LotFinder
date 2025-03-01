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

public class LotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lot);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the selected lot's data from the previous activity
        Bundle bundle = getIntent().getExtras();
        Lot lot = (Lot) bundle.getSerializable("lot");

        // Initialize UI elements
        TextView tv_distance = findViewById(R.id.tv_distance);
        TextView tv_vacancy = findViewById(R.id.tv_vacancy);
        TextView tv_prices = findViewById(R.id.tv_prices);
        TextView tv_name = findViewById(R.id.tv_name);

        // Set lot details in TextViews
        tv_distance.setText(lot.getDistance() + "m");
        tv_vacancy.setText(lot.getVacancy());
        tv_prices.setText(lot.getPrices());
        tv_name.setText(lot.getName());

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

        // Add Toast Messages for Buttons
        Button googleMapsButton = findViewById(R.id.googlemaps_button);
        Button wazeButton = findViewById(R.id.waze_button);

        // When Google Maps button is clicked, show a Toast
        googleMapsButton.setOnClickListener(v ->
                Toast.makeText(LotActivity.this, "Will close app and open Google Maps\n(Relevant for 2nd Semester)", Toast.LENGTH_LONG).show()
        );

        // When Waze button is clicked, show a Toast
        wazeButton.setOnClickListener(v ->
                Toast.makeText(LotActivity.this, "Will close app and open Waze\n(Relevant for 2nd Semester)", Toast.LENGTH_LONG).show()
        );
    }
}
