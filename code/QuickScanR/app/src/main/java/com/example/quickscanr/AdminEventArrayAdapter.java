package com.example.quickscanr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminEventArrayAdapter extends RecyclerView.Adapter<AdminEventItemView>{
    private final ArrayList<Event> events;
    private final Context context;
    private final buttonListener listener;

    public AdminEventArrayAdapter(Context context, ArrayList<Event> events, buttonListener listener) {
        this.events = events;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminEventItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_event_list, parent, false);
        return new AdminEventItemView(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminEventItemView holder, int position) {
        Event event = events.get(position);
        holder.eventTitle.setText(event.getName());
        holder.eventImage.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface buttonListener {
        void onClickButton(int position);

    }

}
