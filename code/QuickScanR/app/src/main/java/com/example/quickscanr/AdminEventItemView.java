package com.example.quickscanr;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class AdminEventItemView extends RecyclerView.ViewHolder {
    public TextView eventTitle;
    public ImageView eventImage;

    ImageButton editEvent;

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

//
    }
}