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

public class LotFinderAdapter extends RecyclerView.Adapter<LotViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Lot> lots;
    private String sortType;

    public LotFinderAdapter(String sortType) {
        lots = new ArrayList<>();
        this.sortType = sortType;

        db.collection("Lots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    lots.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Lot lot = new Lot(
                                document.get("Name").toString(),
                                document.get("Rating").toString(),
                                document.get("Prices").toString(),
                                document.get("Vacancy").toString(),
                                document.get("Distance").toString(),
                                document.get("ID").toString()
                        );
                        lots.add(lot);
                    }
                    lots = sortedLots(lots);
                    notifyDataSetChanged();
                }
            }
        });

        db.collection("Lots").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    lots.clear();
                    for (QueryDocumentSnapshot document : value) {
                        Lot lot = new Lot(
                                document.get("Name").toString(),
                                document.get("Rating").toString(),
                                document.get("Prices").toString(),
                                document.get("Vacancy").toString(),
                                document.get("Distance").toString(),
                                document.get("ID").toString()
                        );
                        lots.add(lot);
                    }
                    lots = sortedLots(lots);
                    notifyDataSetChanged();
                }
            }
        });
    }

    private List<Lot> sortedLots(List<Lot> lots) {
        List<Lot> sorted = new ArrayList<>(lots);

        if ("Vacancy".equalsIgnoreCase(sortType)) {
            sorted.sort((a, b) -> {
                int vacantA = parseVacantSpots(a.getVacancy());
                int vacantB = parseVacantSpots(b.getVacancy());
                return Integer.compare(vacantB, vacantA);
            });
        } else {
            sorted.sort((a, b) -> {
                int vacantA = parseVacantSpots(a.getVacancy());
                int vacantB = parseVacantSpots(b.getVacancy());

                if (vacantA == 0 && vacantB != 0) return 1;
                if (vacantB == 0 && vacantA != 0) return -1;

                double distA = parseDistance(a.getDistance());
                double distB = parseDistance(b.getDistance());
                return Double.compare(distA, distB);
            });
        }

        return sorted;
    }

    private int parseVacantSpots(String vacancy) {
        try {
            String[] parts = vacancy.split("/");
            return Integer.parseInt(parts[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDistance(String distance) {
        try {
            return Double.parseDouble(distance.trim());
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }

    @NonNull
    @Override
    public LotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lot, parent, false);
        return new LotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LotViewHolder holder, int position) {
        Lot lot = lots.get(position);
        holder.tv_name.setText(lot.getName());
        holder.tv_rating.setText(String.valueOf(lot.getRating()));
        holder.tv_prices.setText(lot.getPrices());
        holder.tv_vacancy.setText(lot.getVacancy());
        holder.tv_distance.setText(String.valueOf(lot.getDistance()));

        int vacantSpots = parseVacantSpots(lot.getVacancy());
        if (vacantSpots == 0) {
            holder.card.setCardBackgroundColor(holder.card.getContext().getResources().getColor(android.R.color.darker_gray));
        } else {
            holder.card.setCardBackgroundColor(holder.card.getContext().getResources().getColor(R.color.blue));
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LotActivity.class);
                intent.putExtra("lot", lot);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) v.getContext(),
                        holder.card,
                        "lottransition"
                );
                v.getContext().startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lots.size();
    }
}
