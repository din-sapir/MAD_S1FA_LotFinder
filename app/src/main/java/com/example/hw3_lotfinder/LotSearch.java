package com.example.hw3_lotfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LotSearch extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private RadioGroup radioGroup;

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

        // Info button
        FloatingActionButton floatingActionButton = findViewById(R.id.btn_info);
        floatingActionButton.setOnClickListener(v -> {
            Intent i = new Intent(LotSearch.this, Information.class);
            startActivity(i);
        });

        EditText locationET = findViewById(R.id.pt_search);
        Button submit_btn = findViewById(R.id.btn_continue);
        Button preciseLocationBtn = findViewById(R.id.btn_location);
        radioGroup = findViewById(R.id.radioGroup);

        submit_btn.setOnClickListener(view -> {
            String query = locationET.getText().toString();
            locationET.setText("");
            String sortType = getSortType();
            performSearch(query, sortType);
        });

        preciseLocationBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                usePreciseLocation();
            }
        });
    }

    private void usePreciseLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }

        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Intent i = new Intent(LotSearch.this, MapView.class);
            i.putExtra("lat", String.valueOf(lat));
            i.putExtra("lng", String.valueOf(lng));
            i.putExtra("query", "Your Location");
            i.putExtra("sort", getSortType());
            startActivity(i);
        } else {
            Toast.makeText(this, "Location not available. Try again or check permissions.", Toast.LENGTH_LONG).show();
        }
    }

    private void performSearch(String query, String sortType) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String result = "";
            try {
                URL api = new URL("https://maps.google.com/maps/api/geocode/json?address=" + query + "&key=AIzaSyC3CuDYBjtwGhAvOYDG4kKRYrSybuoIH24");
                BufferedReader in = new BufferedReader(new InputStreamReader(api.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    result += inputLine;
                }
                in.close();

                Gson gson = new Gson();
                JsonObject resultObj = gson.fromJson(result, JsonObject.class);
                JsonObject location = resultObj.getAsJsonArray("results").get(0).getAsJsonObject()
                        .getAsJsonObject("geometry").getAsJsonObject("location");
                String lat = location.get("lat").getAsString();
                String lng = location.get("lng").getAsString();

                String finalLat = lat;
                String finalLng = lng;

                handler.post(() -> {
                    Intent i = new Intent(LotSearch.this, MapView.class);
                    i.putExtra("lat", finalLat);
                    i.putExtra("lng", finalLng);
                    i.putExtra("query", query);
                    i.putExtra("sort", sortType);
                    startActivity(i);
                });

            } catch (IOException | RuntimeException e) {
                handler.post(() -> Toast.makeText(LotSearch.this, "Location not found.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String getSortType() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radioButton_Vacancy) {
            return "Vacancy";
        } else if (selectedId == R.id.radioButton_Rating) {
            return "Rating";
        }
        else {
            return "Distance";
        }
    }
}