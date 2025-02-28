package com.example.hw3_lotfinder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

// ViewHolder for displaying parking lot information in RecyclerView
public class LotViewHolder extends RecyclerView.ViewHolder {

    public TextView tv_name;
    public TextView tv_rating;
    public TextView tv_prices;
    public TextView tv_vacancy;
    public TextView tv_distance;

    public CardView card;

    public LotViewHolder(@NonNull View itemView) {
        super(itemView);
        // Initialize TextViews for displaying lot details
        this.tv_name = itemView.findViewById(R.id.tv_name);
        this.tv_rating = itemView.findViewById(R.id.tv_rating);
        this.tv_prices = itemView.findViewById(R.id.tv_prices);
        this.tv_vacancy = itemView.findViewById(R.id.tv_vacancy);
        this.tv_distance = itemView.findViewById(R.id.tv_distance);
        this.card = itemView.findViewById(R.id.CardView);
    }
}
