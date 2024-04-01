package com.example.quickscanr;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Announcement adapter: allows us to add values in our RecyclerView
 * Represents the adapter that for announcements to display in the UI
 * @see Announcement
 * @see AnnouncementViewHolder
 */
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementViewHolder> {

    private ArrayList<Announcement> announcements;
    /**
     * Constructor to instantiate our adapter with our Announcement list
     */
    public AnnouncementAdapter(ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }



    /**
     *
     * The actual inflation of our ViewHolder to present in our UI
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return ViewHolder
     */
    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and return a new ViewHolder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_item, parent, false);
        return new AnnouncementViewHolder(itemView);
    }

    /**
     * Updates the UI for a specific position
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        // Bind data to views in the ViewHolder
        Announcement announcement = announcements.get(position);
        holder.titleTextView.setText(announcement.getTitle());
        holder.organizerTextView.setText(String.valueOf(announcement.getUserName()));
        holder.bodyTextView.setText(announcement.getBody());
        holder.profImageView.setImageBitmap(announcement.getBitmap());
    }


    /**
     * Returns the amount of items in the dataset
     * @return integer, the size of the dataset.
     */
    @Override
    public int getItemCount() {
        // Return the size of the dataset
        return announcements.size();
    }
}


