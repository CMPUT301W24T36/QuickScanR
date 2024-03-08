package com.example.quickscanr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

/**
 * adapter class for the RecyclerView displaying checked-in attendees
 * manages the data of attendees and binds it to views that are displayed within a RecyclerView
 *
 */
a
public class CheckedInAttendeeAdapter extends RecyclerView.Adapter<CheckedInAttendeeAdapter.ViewHolder> {
    private List<Map<String, Object>> attendeeDataList;
    private OnItemClickListener onItemClickListener;

    /**
     * interface definition for a callback - invoked when an item in this adapter is clicked
     */

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * constructor for initializing the adapter with a list of attendees' data
     * @param attendeeDataList : data of checked-in attendees to be displayed in the RecyclerView
     */

    public CheckedInAttendeeAdapter(List<Map<String, Object>> attendeeDataList) {
        this.attendeeDataList = attendeeDataList;
    }

    /**
     * sets a listener for item click events
     * @param listener The listener to set
     */

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * called when RecyclerView needs a new ViewHolder of the given type to represent an item
     * @param parent : ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType : view type of the new View
     * @return : new ViewHolder that holds a View of the given view type
     */

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_user_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * called by RecyclerView to display the data at the specified position
     * @param holder : ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * @param position : position of the item within the adapter's data set
     */


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> attendeeData = attendeeDataList.get(position);
        String name = (String) attendeeData.get("name");
        List<Timestamp> checkIns = (List<Timestamp>) attendeeData.get("checkIns");
        holder.nameTextView.setText(name);
        holder.checkInCountTextView.setText(String.valueOf(checkIns != null ? checkIns.size() : 0));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (onItemClickListener != null && pos != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(pos);
                }
            }
        });
    }


    /**
     * returns the total number of items in the data set held by the adapter
     * @return : size of the attendeeDataList
     */

    @Override
    public int getItemCount() {
        return attendeeDataList.size();
    }

    /**
     * ViewHolder describes an item view and metadata about its place within the RecyclerView
     */

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView checkInCountTextView;

        /**
         * constructor for the ViewHolder - used to find and store views (for faster access)
         * @param view : view held by the ViewHolder
         */

        ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.chkd_usr_text);
            checkInCountTextView = view.findViewById(R.id.chkd_usr_count);
        }
    }
}