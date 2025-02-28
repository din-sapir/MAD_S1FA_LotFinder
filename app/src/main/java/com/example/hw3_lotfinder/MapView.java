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

// Activity for displaying the map view
public class MapView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_view);

        // Apply system bar insets to avoid overlapping UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));

        // Define gradient colors
        int white = getResources().getColor(R.color.white);
        int lightBlue = getResources().getColor(R.color.light_blue);

        // Create a gradient background transitioning from white to light blue
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,  // Gradient direction
                new int[]{white, white, lightBlue}       // Extended white section before transitioning
        );
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setGradientCenter(0.5f, 0.85f);  // Start transition at 85% of screen height

        // Apply the gradient background to the activity
        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(gradientDrawable);

        // Initialize switch for toggling between views
        Switch switchView = findViewById(R.id.s_ViewSwitch);

        // Set switch state to ON since the user is in MapView
        switchView.setChecked(true);

        // Handle switch toggle to navigate back to MainActivity
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {  // If turned OFF, switch back to MainActivity
                Intent intent = new Intent(MapView.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Smooth transition
                finish(); // Close this activity to prevent returning via the back button
            }
        });
    }
}