package com.example.quickscanr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Milestone adapter: allows us to add values in our RecyclerView
 */
public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.ViewHolder> {

    private List<Milestone> milestoneList;

    /**
     * Constructor to instantiate our adapter with our milestone list
     * @param milestoneList
     */
    public MilestoneAdapter(List<Milestone> milestoneList) {
        this.milestoneList = milestoneList;
    }

    /**
     * ViewHolder represents our object (milestone) in the RecyclerView, per row.
     * "Holds" each milestone item!
     * I kept it in this file, but if we want to, we can make it its own class.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize views in the ViewHolder
            titleTextView = itemView.findViewById(R.id.milestone_item_title);
            descriptionTextView = itemView.findViewById(R.id.milestone_description);
        }
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and return a new ViewHolder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.milestone_item, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Updates the UI for a specific position
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to views in the ViewHolder
        Milestone milestone = milestoneList.get(position);
        holder.titleTextView.setText(milestone.getTitle());
        holder.descriptionTextView.setText(milestone.getDescription());
    }


    /**
     *
     * @return integer, the size of the dataset.
     */
    @Override
    public int getItemCount() {
        // Return the size of the dataset
        return milestoneList.size();
    }
}


