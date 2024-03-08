package com.example.quickscanr;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.quickscanr.R;

/**
 * ViewHolder represents our object (announcement) in the RecyclerView, per row.
 * @see Announcement
 */
public class AnnouncementViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTextView;
    public TextView organizerTextView;

    public TextView bodyTextView;

    /**
     * Initialize views in the ViewHolder
     * @param itemView
     */
    public AnnouncementViewHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.announcement_item_title);
        organizerTextView = itemView.findViewById(R.id.announcement_item_organizer);
        bodyTextView = itemView.findViewById(R.id.announcement_item_body);

    }
}