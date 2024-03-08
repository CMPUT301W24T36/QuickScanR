package com.example.quickscanr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * AdminEventArrayAdapter
 * - - this class is for populating the recycler view with different events
 *  * - the methods are for inflating views, binding positions and getting the size of user list
 */
public class AdminEventArrayAdapter extends RecyclerView.Adapter<AdminEventItemView>{
    private final ArrayList<Event> events;
    private final Context context;
    private final buttonListener listener;

    /**
     * AdminEventArrayAdapter:
     *
     * @param context: the state/context how the adapter is being used
     * @param events: list of events that is being displayed
     * @param listener : for keeping track of clicked buttons for the events
     */
    public AdminEventArrayAdapter(Context context, ArrayList<Event> events, buttonListener listener) {
        this.events = events;
        this.context = context;
        this.listener = listener;
    }

    /**
     *onCreateViewHolder:
     *  - to create a new viewHolder whenever the recycler need it for a new item
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     *  - new AdminEventItemView(view, listener);
     *      - returning a new instance for representing an item
     */
    @NonNull
    @Override
    public AdminEventItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_event_list, parent, false);
        return new AdminEventItemView(view, listener);
    }

    /**
     * onBindViewHolder
     *  - this is for populating the information inside each item (title, poster)
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull AdminEventItemView holder, int position) {
        Event event = events.get(position);
        holder.eventTitle.setText(event.getName());
        holder.eventImage.setImageResource(R.drawable.ic_launcher_background);
    }

    /**
     * getItemCount
     *  - gets the user count
     * @return: the size of the list
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * buttonListener
     *  - interface for listening to clicked buttons
     */
    public interface buttonListener {
        /**
         * onClickButton
         *  - called when a event is clicked
         * @param position : position of the clicked item
         */
        void onClickButton(int position);

    }

}
