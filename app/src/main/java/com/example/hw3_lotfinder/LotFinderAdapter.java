package com.example.hw3_lotfinder;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * LotFinderAdapter
 * ----------------
 * RecyclerView adapter for displaying parking lot cards retrieved from Firebase Firestore.
 * Dynamically listens for changes in the "Lots" collection and sorts them by selected criteria.
 *
 * Supports scene transition animation to LotActivity on card click.
 */
public class LotFinderAdapter extends RecyclerView.Adapter<LotViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore database instance
    private List<Lot> lots;           // List of current lot data to display
    private String sortType;          // User-selected sorting preference (Distance, Rating, Vacancy)
    private String userLatStr;        // Latitude of user's location (as string)
    private String userLngStr;        // Longitude of user's location (as string)

    /**
     * Constructor: Fetches data from Firestore and listens for live updates.
     *
     * @param sortType   Sorting criteria (Distance, Rating, or Vacancy)
     * @param userLat    User's latitude string (used for calculating distance)
     * @param userLng    User's longitude string
     */
    public LotFinderAdapter(String sortType, String userLat, String userLng) {
        lots = new ArrayList<>();
        this.sortType = sortType;
        this.userLatStr = userLat;
        this.userLngStr = userLng;

        // Initial one-time fetch of all lots
        db.collection("Lots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    lots.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Lot lot = createLotFromDocument(document);
                        lots.add(lot);
                    }
                    lots = sortedLots(lots); // Sort based on selected criteria
                    notifyDataSetChanged();  // Notify RecyclerView to refresh
                }
            }
        });

        // Listen for real-time updates in the "Lots" collection
        db.collection("Lots").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    lots.clear();
                    for (QueryDocumentSnapshot document : value) {
                        Lot lot = createLotFromDocument(document);
                        lots.add(lot);
                    }
                    lots = sortedLots(lots);
                    notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Converts a Firestore document to a Lot object and calculates distance.
     */
    private Lot createLotFromDocument(QueryDocumentSnapshot document) {
        Lot lot = new Lot(
                document.get("Name").toString(),
                document.get("Rating").toString(),
                document.get("Prices").toString(),
                document.get("Vacancy").toString(),
                document.get("lat").toString(),
                document.get("lng").toString(),
                document.get("ID").toString()
        );

        try {
            double lat = Double.parseDouble(userLatStr);
            double lng = Double.parseDouble(userLngStr);
            lot.calculateDistance(lat, lng); // Calculate distance from user to lot
        } catch (NumberFormatException e) {
            // Fallback if lat/lng is not parsable; distance will default to MAX
        }

        return lot;
    }

    /**
     * Sorts the list of lots based on the selected sort type.
     */
    private List<Lot> sortedLots(List<Lot> lots) {
        List<Lot> sorted = new ArrayList<>(lots);

        if ("Vacancy".equalsIgnoreCase(sortType)) {
            // Sort by available spots (descending)
            sorted.sort((a, b) -> Integer.compare(
                    parseVacantSpots(b.getVacancy()),
                    parseVacantSpots(a.getVacancy()))
            );

        } else if ("Rating".equalsIgnoreCase(sortType)) {
            // Sort by rating (descending), move zero-vacancy lots to bottom
            sorted.sort((a, b) -> {
                int vacantA = parseVacantSpots(a.getVacancy());
                int vacantB = parseVacantSpots(b.getVacancy());

                if (vacantA == 0 && vacantB != 0) return 1;
                if (vacantB == 0 && vacantA != 0) return -1;

                float ratingA = parseRating(a.getRating());
                float ratingB = parseRating(b.getRating());
                return Float.compare(ratingB, ratingA); // Higher rated first
            });

        } else {
            // Default: sort by distance (ascending), move zero-vacancy lots to bottom
            sorted.sort((a, b) -> {
                int vacantA = parseVacantSpots(a.getVacancy());
                int vacantB = parseVacantSpots(b.getVacancy());

                if (vacantA == 0 && vacantB != 0) return 1;
                if (vacantB == 0 && vacantA != 0) return -1;

                return Double.compare(a.getDistanceInMeters(), b.getDistanceInMeters());
            });
        }

        return sorted;
    }

    /**
     * Parses the number of available spots from a vacancy string like "3 / 10".
     */
    private int parseVacantSpots(String vacancy) {
        try {
            String[] parts = vacancy.split("/");
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Parses a rating string like "4.3" into a float.
     */
    private float parseRating(String ratingStr) {
        try {
            return Float.parseFloat(ratingStr);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    /**
     * Inflates the card layout for each lot row.
     */
    @NonNull
    @Override
    public LotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lot, parent, false);
        return new LotViewHolder(view);
    }

    /**
     * Binds data to the UI elements of each lot card.
     */
    @Override
    public void onBindViewHolder(@NonNull LotViewHolder holder, int position) {
        Lot lot = lots.get(position);

        // Set UI elements
        holder.tv_name.setText(lot.getName());
        holder.tv_rating.setText(String.valueOf(lot.getRating()));
        holder.tv_prices.setText(lot.getPrices());
        holder.tv_vacancy.setText(lot.getVacancy());
        holder.tv_distance.setText(lot.getDistance());

        // Set card background color based on vacancy
        int vacantSpots = parseVacantSpots(lot.getVacancy());
        if (vacantSpots == 0) {
            holder.card.setCardBackgroundColor(holder.card.getContext().getResources().getColor(android.R.color.darker_gray));
        } else {
            holder.card.setCardBackgroundColor(holder.card.getContext().getResources().getColor(R.color.blue));
        }

        // Set click listener to open LotActivity with shared transition
        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), LotActivity.class);
            intent.putExtra("lot", lot); // Pass the selected lot

            // Set up shared element transition (card view)
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) v.getContext(),
                    holder.card,
                    "lottransition"
            );

            v.getContext().startActivity(intent, options.toBundle());
        });
    }

    /**
     * Returns the total number of lot items to be displayed.
     */
    @Override
    public int getItemCount() {
        return lots.size();
    }
}
