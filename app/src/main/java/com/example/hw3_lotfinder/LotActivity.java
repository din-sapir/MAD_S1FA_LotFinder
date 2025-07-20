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

    private boolean fromMap = false; // ← Track if launched from MapView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lot);

        // Apply system bar insets
        View statusBarBackground = findViewById(R.id.statusBarBackground);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            statusBarBackground.getLayoutParams().height = systemBars.top;
            statusBarBackground.requestLayout();
            return insets;
        });

        // Retrieve the selected lot's data
        Bundle bundle = getIntent().getExtras();
        Lot lot = (Lot) bundle.getSerializable("lot");
        fromMap = getIntent().getBooleanExtra("fromMap", false); // ← Retrieve fromMap flag

        // Initialize UI elements
        TextView tv_distance = findViewById(R.id.tv_distance);
        TextView tv_vacancy = findViewById(R.id.tv_vacancy);
        TextView tv_prices = findViewById(R.id.tv_prices);
        TextView tv_name = findViewById(R.id.tv_name);

        // Set lot details
        tv_distance.setText(lot.getDistance());
        tv_vacancy.setText(lot.getVacancy());
        tv_prices.setText(lot.getPrices());
        tv_name.setText(lot.getName());

        // Set status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));

        // Set gradient background
        int white = getResources().getColor(R.color.white);
        int lightBlue = getResources().getColor(R.color.light_blue);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{white, white, lightBlue}
        );
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setGradientCenter(0.5f, 0.85f);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackground(gradientDrawable);

        // Set up toast messages
        Button googleMapsButton = findViewById(R.id.googlemaps_button);
        Button wazeButton = findViewById(R.id.waze_button);

        googleMapsButton.setOnClickListener(v ->
                Toast.makeText(LotActivity.this, "Will close app and open Google Maps", Toast.LENGTH_LONG).show()
        );

        wazeButton.setOnClickListener(v ->
                Toast.makeText(LotActivity.this, "Will close app and open Waze", Toast.LENGTH_LONG).show()
        );
    }

    @Override
    public void finish() {
        super.finish();
        if (fromMap) {
            overridePendingTransition(0, R.anim.zoom_exit);
        }
    }
}
