package com.example.hw3_lotfinder;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Information Activity
 * --------------------
 * This activity serves as a static informational screen for the user.
 * It displays a clean UI with a gradient background and accounts for system insets
 * to ensure proper padding relative to the status and navigation bars.
 */
public class Information extends AppCompatActivity {

    /**
     * Initializes the Information screen and applies layout and UI styling.
     * Handles edge-to-edge display configuration and background gradient.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables edge-to-edge layout rendering
        setContentView(R.layout.activity_information);

        // Apply system bar (status/nav) padding to the main container
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // -------------------------------
        // Gradient Background Setup
        // -------------------------------

        // Define the colors to be used in the gradient
        int white = getResources().getColor(R.color.white);       // Base color
        int lightBlue = getResources().getColor(R.color.light_blue); // Accent color

        // Create a vertical gradient drawable from white to light blue
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, // Top to bottom gradient
                new int[]{white, white, lightBlue}       // Two white stops before transition
        );

        // Make the gradient more subtle by starting the transition low on the screen
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setGradientCenter(0.5f, 0.85f);  // X=50%, Y=85%

        // Apply the gradient as background to the root view of the activity
        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(gradientDrawable);
    }
}