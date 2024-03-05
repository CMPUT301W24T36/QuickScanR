package com.example.quickscanr;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder represents our object (milestone) in the RecyclerView, per row.
 * "Holds" each milestone item!
 * I kept it in this file, but if we want to, we can make it its own class.
 */
public class MilestoneViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTextView;
    public TextView descriptionTextView;

    public MilestoneViewHolder(View itemView) {
        super(itemView);
        // Initialize views in the ViewHolder
        titleTextView = itemView.findViewById(R.id.milestone_item_title);
        descriptionTextView = itemView.findViewById(R.id.milestone_description);
    }
}