package com.example.hw3_lotfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * MapView Activity
 * ----------------
 * Displays parking lots on a Google Map.
 * Allows toggling back to list view, fetching lot data from Firestore,
 * dynamically coloring markers by vacancy, showing toast on full lots,
 * and navigating to LotActivity with transitions.
 */
public class MapView extends AppCompatActivity {

    private String lat;       // Latitude passed from previous screen
    private String lng;       // Longitude passed from previous screen
    private String query;     // Display string of selected location
    private String sort;      // Sort method passed from previous screen
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;   // Google Map instance
    private boolean usePreciseLocation = false; // Whether to use real-time GPS location
    private final Map<Marker, Lot> markerLotMap = new HashMap<>(); // Mapping between markers and Lot objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_view);

        // Set status bar padding for edge-to-edge layout
        View statusBarBackground = findViewById(R.id.statusBarBackground);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            statusBarBackground.getLayoutParams().height = systemBars.top;
            statusBarBackground.requestLayout();
            return insets;
        });

        // Set custom status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));

        // Apply background gradient to activity root
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

        // Handle switch toggle to go back to MainActivity (list view)
        Switch switchView = findViewById(R.id.s_ViewSwitch);
        switchView.setChecked(true); // Stay on map by default
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                Intent intent = new Intent(MapView.this, MainActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("query", query);
                intent.putExtra("sort", sort);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // Close map activity
            }
        });

        // Retrieve coordinates and query text from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getString("lat", "0.0");
            lng = extras.getString("lng", "0.0");
            query = extras.getString("query", "");
            sort = extras.getString("sort", "Distance");
        }

        // Initialize and prepare Google Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapView.this, R.raw.map_style));
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Show user location or default to selected input
            if (usePreciseLocation) {
                enableMyLocation();
            } else {
                LatLng pos = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));
                mMap.addMarker(new MarkerOptions().position(pos).title(query));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
            }

            // Fetch and render parking lots on the map
            fetchDataFromFirebase();

            // Handle user clicks on lot markers
            mMap.setOnMarkerClickListener(marker -> {
                Lot clickedLot = markerLotMap.get(marker);
                if (clickedLot != null) {
                    int vacantSpots = parseVacantSpots(clickedLot.getVacancy());
                    if (vacantSpots == 0) {
                        // Show toast if lot is full
                        Toast.makeText(MapView.this, "This Lot is Full", Toast.LENGTH_SHORT).show();
                    }

                    // Animate camera to marker and open LotActivity
                    LatLng markerPosition = marker.getPosition();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(markerPosition), 300, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            Intent intent = new Intent(MapView.this, LotActivity.class);
                            intent.putExtra("lot", clickedLot);
                            intent.putExtra("fromMap", true);
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoom_enter, 0);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                    return true;
                }
                return false;
            });
        });
    }

    /**
     * Enables location layer on the map and moves camera to user's current location.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            // Animate to current location once
            mMap.setOnMyLocationChangeListener(location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                    mMap.setOnMyLocationChangeListener(null); // Only once
                }
            });

        } else {
            // Ask for location permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Retrieves parking lot data from Firestore and adds markers to the map.
     */
    private void fetchDataFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Lots").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        // Parse lot data from Firestore document
                        String name = document.get("Name").toString();
                        String lotLat = document.get("lat").toString();
                        String lotLng = document.get("lng").toString();
                        String rating = document.get("Rating").toString();
                        String prices = document.get("Prices").toString();
                        String vacancy = document.get("Vacancy").toString();

                        // Create Lot object and calculate distance
                        Lot lot = new Lot(name, rating, prices, vacancy, lotLat, lotLng);
                        lot.calculateDistance(Double.parseDouble(lat), Double.parseDouble(lng));

                        // Set marker position and hue based on vacancy
                        LatLng lotPosition = new LatLng(Double.parseDouble(lotLat), Double.parseDouble(lotLng));
                        int vacantSpots = parseVacantSpots(vacancy);
                        float markerHue = (vacantSpots == 0)
                                ? BitmapDescriptorFactory.HUE_AZURE  // Special color for full lots
                                : BitmapDescriptorFactory.HUE_BLUE;  // Regular color otherwise

                        // Add marker to map and link to Lot object
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(lotPosition)
                                .title(name)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerHue)));
                        markerLotMap.put(marker, lot);

                    } catch (Exception e) {
                        Log.e("MapView", "Error parsing lot coordinates", e);
                    }
                }
            } else {
                Log.e("MapView", "Error loading lots from Firestore", task.getException());
            }
        });
    }

    /**
     * Parses the number of available spots from a vacancy string like "3 / 10".
     */
    private int parseVacantSpots(String vacancy) {
        try {
            String[] parts = vacancy.split("/");
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            return 0; // Default to 0 if parsing fails
        }
    }
}
