package com.example.hw3_lotfinder;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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


        EditText locationET = findViewById(R.id.PT_Search);
        Button submit_btn = findViewById(R.id.btn_continue);
        RadioGroup radioGroup = findViewById(R.id.radioGroup); // You must set this ID in the XML!
        RadioButton radioVacancy = findViewById(R.id.radioButton_Vacancy);
        RadioButton radioDistance = findViewById(R.id.radioButton_Distance);


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = locationET.getText().toString();
                locationET.setText("");
                int selectedId = radioGroup.getCheckedRadioButtonId();
                String sortType = "";

                if (selectedId == R.id.radioButton_Vacancy) {
                    sortType = "Vacancy";
                } else if (selectedId == R.id.radioButton_Distance) {
                    sortType = "Distance";
                }
                performSearch(query,sortType);
            }

        });
    }
    private void performSearch(String query, String sortType) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                String result = "";
                try {
                    URL api = new URL("https://maps.google.com/maps/api/geocode/json?address="+query+"&key=AIzaSyC3CuDYBjtwGhAvOYDG4kKRYrSybuoIH24");
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(api.openStream()));
                    String inputLine;

                    while ((inputLine=in.readLine()) != null) {
                        result += inputLine;
                    }
                    in.close();

                    Gson gson = new Gson();
                    JsonObject resultObj = gson.fromJson(result,JsonObject.class);
                    JsonObject location = resultObj.getAsJsonArray("results").get(0).getAsJsonObject()
                            .getAsJsonObject("geometry").getAsJsonObject("location");
                    String lat = location.get("lat").getAsString();
                    String lng = location.get("lng").getAsString();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //TextView resultTV = findViewById(R.id.resultTV);
                            //resultTV.setText(lat+", "+lng);
                            Intent i = new Intent(LotSearch.this,MainActivity.class);
                            //Intent a = new Intent(LotSearch.this,LotFinderAdapter.class);
                            i.putExtra("lat",lat);
                            i.putExtra("lng",lng);
                            i.putExtra("query",query);
                            i.putExtra("sort",sortType);
                            //a.putExtra("sort",sortType);
                            //startActivity(a);
                            startActivity(i);
                        }
                    });

                } catch (IOException | RuntimeException e) {


                }
            }
        });
    }
}