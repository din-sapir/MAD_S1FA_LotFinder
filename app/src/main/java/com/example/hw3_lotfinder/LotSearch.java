package com.example.hw3_lotfinder;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LotSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lot_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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


       // not sure if work:
        FloatingActionButton floatingActionButton = findViewById(R.id.btn_info);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //throw new RuntimeException("Test Crash"); // Force Crash
                //startActivity(i); //KEEP AS COMMENT
                Intent i = new Intent(LotSearch.this, Information.class);
                //activityResultLauncher.launch(i);
                startActivity(i);
            }
        });

        EditText locationET = findViewById(R.id.locationET);
        Button button = findViewById(R.id.button_continue);
    }
}