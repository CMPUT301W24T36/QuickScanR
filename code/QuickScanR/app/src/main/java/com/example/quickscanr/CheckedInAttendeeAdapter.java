package com.example.quickscanr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class CheckedInAttendeeAdapter extends RecyclerView.Adapter<CheckedInAttendeeAdapter.ViewHolder> {

    private List<User> attendees;
    private HashMap<String, Integer> checkedInCountsByEmail; // Keyed by user email
    private LayoutInflater inflater;

    public CheckedInAttendeeAdapter(Context context, List<User> attendees, HashMap<String, Integer> checkedInCountsByEmail) {
        this.attendees = attendees;
        this.checkedInCountsByEmail = checkedInCountsByEmail;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.checked_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User attendee = attendees.get(position);
        Integer checkInCount = checkedInCountsByEmail.getOrDefault(attendee.getEmail(), 0); // Use email as key

        holder.checkedUserName.setText(attendee.getName());
        holder.checkedUserCount.setText(String.valueOf(checkInCount));
        // Implement image loading here if needed
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    public void updateData(List<User> newAttendees, HashMap<String, Integer> newCheckedInCountsByEmail) {
        this.attendees = newAttendees;
        this.checkedInCountsByEmail = newCheckedInCountsByEmail;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView checkedUserImage; // If you're displaying user images
        TextView checkedUserName, checkedUserCount;

        public ViewHolder(View itemView) {
            super(itemView);
            checkedUserImage = itemView.findViewById(R.id.chkd_usr_image);
            checkedUserName = itemView.findViewById(R.id.chkd_usr_text);
            checkedUserCount = itemView.findViewById(R.id.chkd_usr_count);
        }
    }
}

