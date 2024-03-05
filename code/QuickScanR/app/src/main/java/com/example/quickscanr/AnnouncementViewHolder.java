package com.example.quickscanr;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quickscanr.R;

/**
 * ViewHolder represents our object (announcement) in the RecyclerView, per row.
 * Can be its own class
 */
public class AnnouncementViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTextView;
    public TextView descriptionTextView;

    public AnnouncementViewHolder(View itemView) {
        super(itemView);
        // Initialize views in the ViewHolder
        titleTextView = itemView.findViewById(R.id.milestone_item_title);
        descriptionTextView = itemView.findViewById(R.id.milestone_description);
    }
}