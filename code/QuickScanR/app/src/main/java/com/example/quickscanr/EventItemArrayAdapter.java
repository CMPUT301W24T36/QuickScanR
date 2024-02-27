package com.example.quickscanr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Event array adapter for RecyclerView
 * (Similar to ListyCity's but some functionality is in EventItemViewHolder)
 */
public class EventItemArrayAdapter extends RecyclerView.Adapter<EventItemViewHolder> {
    private final ArrayList<Event> events;
    private final Context context;
    private final OnItemClickListener listener;

    public EventItemArrayAdapter(Context context, ArrayList<Event> events, OnItemClickListener listener) {
        this.events = events;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventItemViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventItemViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventTitle.setText(event.getName());
        holder.eventDesc.setText(event.getDescription());
        holder.eventImage.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
