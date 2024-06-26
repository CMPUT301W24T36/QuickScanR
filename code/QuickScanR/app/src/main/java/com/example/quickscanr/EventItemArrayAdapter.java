/**
 * array adapter for events in the attendee event list
 */

package com.example.quickscanr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Event array adapter for RecyclerView
 * (Similar to ListyCity's but some functionality is in EventItemViewHolder)
 * @see Event
 * @see EventItemViewHolder
 */
public class EventItemArrayAdapter extends RecyclerView.Adapter<EventItemViewHolder> {
    private final ArrayList<Event> events;
    private final Context context;
    private final OnItemClickListener listener;

    /**
     * Constructor
     * @param context
     * @param events
     * @param listener
     */
    public EventItemArrayAdapter(Context context, ArrayList<Event> events, OnItemClickListener listener) {
        this.events = events;
        this.context = context;
        this.listener = listener;
    }

    /**
     * The actual inflation of our ViewHolder to present in our UI
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public EventItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventItemViewHolder(view, listener);
    }

    /**
     * Updates the UI for a specific position
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventItemViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventTitle.setText(event.getName());
        holder.eventDesc.setText(event.getDescription());
        loadProfileImage(holder.eventImage, event.getOrganizer());
    }

    private void loadProfileImage(ImageView imageView, User organizer) {
        String userId = organizer.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            Log.e("loadProfileImage", "User ID is null or empty for organizer: " + organizer.getName());

            return;
        }

        // Assuming ProfileImage class has been correctly implemented to handle fetching of the image
        ProfileImage.getProfileImage(context, userId, new ProfileImage.ProfileImageCallback() {
            @Override
            public void onImageReady(Bitmap image) {
                imageView.setImageBitmap(image);
            }
        });
    }


    /**
     * Returns the amount of items in the dataset
     * @return integer, the size of the dataset.
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * Listener
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}