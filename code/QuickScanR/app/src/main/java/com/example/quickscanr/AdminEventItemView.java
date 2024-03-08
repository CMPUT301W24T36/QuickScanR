package com.example.quickscanr;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * AdminEventItemView
 * - this is for what is is being displayed for each event that is listed
 * - also for capturing the position when the item is clicked on
 */
public class AdminEventItemView extends RecyclerView.ViewHolder {
    public TextView eventTitle;
    public ImageView eventImage;

    ImageButton editEvent;

    /**
     * AdminEventItemView:
     *  - represents an item in the recycler view and defines what elements need
     *      to be displayed in the view
     *
     * @param itemView : the view for what is displayed on the layout (name, desc, image, etc)
     * @param listener : listens to when button is clicked and gets position
     */
    public AdminEventItemView(View itemView, AdminEventArrayAdapter.buttonListener listener) {
        super(itemView);
        eventTitle = itemView.findViewById(R.id.event_name_detail);
        eventImage = itemView.findViewById(R.id.event_poster);
        editEvent = itemView.findViewById(R.id.edit_event);

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Log.d("button", "onClick:" + getAdapterPosition());
                listener.onClickButton(position);
            }
        });

    }
}