package com.example.hw3_lotfinder;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Adapter for managing parking lot data in RecyclerView
public class LotFinderAdapter extends RecyclerView.Adapter<LotViewHolder> {

    private List<Lot> lots; // List of parking lots

    public LotFinderAdapter() {
        lots = new ArrayList<>();
        // Adding initial parking lot data
        lots.add(new Lot("Carmel 1", 4.8, "24\n4\n100\n500\n40\n7", "51/64", 1100));
        lots.add(new Lot("Upper Dizengoff", 3.2, "20\n4\n100\n550\n30\n8", "35/70", 800));
        lots.add(new Lot("Frishman Lot", 2.2, "20\n5\n100\n500\n30\n7", "51/64", 600));
        lots.add(new Lot("Carmel 2", 5, "24\n4\n100\n600\n40\n8", "51/64", 900));
        lots.add(new Lot("Golda Lot", 3.5, "25\n6\n120\n550\n40\n9", "12/40", 800));
        lots.add(new Lot("Bazel Lot", 4.8, "18\n4\n180\n480\n35\n7", "0/64", 400));
        lots.add(new Lot("Dov Nov Lot", 1.7, "0\n6\n70\n450\n35\n15", "0/32", 1200));
    }

    @NonNull
    @Override
    public LotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for each item in RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lot, parent, false);
        return new LotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LotViewHolder holder, int position) {
        // Bind parking lot data to ViewHolder
        Lot lot = lots.get(position);
        holder.tv_name.setText(lot.getName());
        holder.tv_rating.setText(String.valueOf(lot.getRating()));
        holder.tv_prices.setText(lot.getPrices());
        holder.tv_vacancy.setText(lot.getVacancy());
        holder.tv_distance.setText(String.valueOf(lot.getDistance()));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),LotActivity.class);
                intent.putExtra("lot", lot);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) v.getContext(),
                        holder.card,
                        "lottransition"
                );
                v.getContext().startActivity(intent,options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lots.size(); // Return the number of items in the list
    }
}
