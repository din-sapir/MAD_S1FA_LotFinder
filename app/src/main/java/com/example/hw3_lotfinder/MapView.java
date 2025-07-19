package com.example.hw3_lotfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MapStyleOptions;

public class MapView extends AppCompatActivity {

    private String lat;
    private String lng;
    private String query;
    private String sort;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private boolean usePreciseLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_view);

        // Apply system bar insets to avoid UI overlap
        View statusBarBackground = findViewById(R.id.statusBarBackground);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            statusBarBackground.getLayoutParams().height = systemBars.top;
            statusBarBackground.requestLayout();
            return insets;
        });

        // Status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));

        // Gradient background
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

        // View switch
        Switch switchView = findViewById(R.id.s_ViewSwitch);
        switchView.setChecked(true);
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                Intent intent = new Intent(MapView.this, MainActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("query", query);
                intent.putExtra("sort", sort);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        // Get intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getString("lat", "0.0");
            lng = extras.getString("lng", "0.0");
            query = extras.getString("query", "");
            sort = extras.getString("sort", "Distance");
        }


        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        String finalLat = lat;
        String finalLng = lng;
        String finalQuery = query;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapView.this, R.raw.map_style));
                mMap.getUiSettings().setZoomControlsEnabled(true);

                if (usePreciseLocation) {
                    enableMyLocation();
                } else {
                    LatLng pos = new LatLng(Float.parseFloat(finalLat), Float.parseFloat(finalLng));
                    mMap.addMarker(new MarkerOptions().position(pos).title(finalQuery));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
                }
            }

            private void enableMyLocation() {
                if (ContextCompat.checkSelfPermission(MapView.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MapView.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mMap.setMyLocationEnabled(true);

                    mMap.setOnMyLocationChangeListener(location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.clear(); // Remove old markers
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                            mMap.setOnMyLocationChangeListener(null); // Only once
                        }
                    });

                } else {
                    ActivityCompat.requestPermissions(MapView.this,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            },
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        });
    }
}
