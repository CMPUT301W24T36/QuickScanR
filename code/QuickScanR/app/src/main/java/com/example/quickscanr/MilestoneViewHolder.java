package com.example.quickscanr;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder represents our object (milestone) in the RecyclerView, per row.
 * "Holds" each milestone item!
 * @see Milestone
 */
public class MilestoneViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTextView;
    public TextView descriptionTextView;
    public TextView eventNameTextView;

    /**
     * Initialize views in the ViewHolder
     * @param itemView
     */
    public MilestoneViewHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.milestone_item_title);
        descriptionTextView = itemView.findViewById(R.id.milestone_description);
        eventNameTextView = itemView.findViewById(R.id.milestone_event_name);
    }
}