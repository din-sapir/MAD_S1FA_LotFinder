package com.example.hw3_lotfinder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * LotViewHolder
 * -------------
 * Custom ViewHolder used to display parking lot details inside a RecyclerView.
 * Holds references to all UI components within a single item layout (lot.xml).
 */
public class LotViewHolder extends RecyclerView.ViewHolder {

    public TextView tv_name;       // TextView for lot name
    public TextView tv_rating;     // TextView for lot rating
    public TextView tv_prices;     // TextView for lot pricing info
    public TextView tv_vacancy;    // TextView for available spots (e.g., "3 / 10")
    public TextView tv_distance;   // TextView for distance from user (e.g., "450m", "1.2km")

    public CardView card;          // Root card view container for styling and click animation

    /**
     * Constructor initializes the ViewHolder and binds UI elements to layout views.
     *
     * @param itemView The root view of the individual lot item in the RecyclerView.
     */
    public LotViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize TextViews for displaying lot details
        this.tv_name = itemView.findViewById(R.id.tv_name);
        this.tv_rating = itemView.findViewById(R.id.tv_rating);
        this.tv_prices = itemView.findViewById(R.id.tv_prices);
        this.tv_vacancy = itemView.findViewById(R.id.tv_vacancy);
        this.tv_distance = itemView.findViewById(R.id.tv_distance);

        // Reference to the CardView for layout styling and shared element transitions
        this.card = itemView.findViewById(R.id.CardView);
    }
}
