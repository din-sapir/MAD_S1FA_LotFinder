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

// Adapter for managing parking lot data in RecyclerView
public class LotFinderAdapter extends RecyclerView.Adapter<LotViewHolder> {

    // Connect to firebase and give us the instance we want to work with
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Lot> lots; // List of parking lots

    public LotFinderAdapter() {
        lots = new ArrayList<>();
        // A-sync gathering of all data in the database whilst checking if the action is successful to remove and repopulate the list
        db.collection("Lots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    lots.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Lot lot = new Lot(document.get("Name").toString(), document.get("Rating").toString(), document.get("Prices").toString(), document.get("Vacancy").toString(), document.get("Distance").toString(), document.get("ID").toString());
                        lots.add(lot);
                    }
                    notifyDataSetChanged();
                }
            }

        });
        // Continuous updating of the list when a change occurs in Firebase database
        db.collection("Lots").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                lots.clear();
                for (QueryDocumentSnapshot document : value) {
                    Lot c = new Lot(document.get("Name").toString(), document.get("Rating").toString(), document.get("Prices").toString(), document.get("Vacancy").toString(), document.get("Distance").toString(), document.get("ID").toString());
                    lots.add(c);
                }
                notifyDataSetChanged();
            }
        });
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
