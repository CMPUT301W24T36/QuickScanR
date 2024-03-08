package com.example.quickscanr;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Event ViewHolder for RecyclerView
 * @see Event
 */
public class EventItemViewHolder extends RecyclerView.ViewHolder {
    public TextView eventTitle;
    public TextView eventDesc;
    public ImageView eventImage;

    /**
     * Binds the relevant information for each "row"/ holder of an event to their relevant view
     * Also sets up a listener for each event
     * @param itemView
     * @param listener
     */
    public EventItemViewHolder(View itemView, EventItemArrayAdapter.OnItemClickListener listener) {
        super(itemView);
        eventTitle = itemView.findViewById(R.id.org_ev_title_text);
        eventDesc = itemView.findViewById(R.id.org_ev_item_desc);
        eventImage = itemView.findViewById(R.id.org_ev_image);

        // setup click listener
        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }
        });
    }
}