package com.example.quickscanr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CheckedInAttendeeAdapter extends RecyclerView.Adapter<CheckedInAttendeeAdapter.ViewHolder> {
    private List<Attendee> attendeeList;

    public CheckedInAttendeeAdapter(List<Attendee> attendeeList) {
        this.attendeeList = attendeeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attendee attendee = attendeeList.get(position);
        holder.nameTextView.setText(attendee.getName());
        holder.checkInCountTextView.setText(String.valueOf(attendee.getCheckInCount()));
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView checkInCountTextView;

        ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.chkd_usr_text);
            checkInCountTextView = view.findViewById(R.id.chkd_usr_count);
        }
    }

    public void updateAttendees(List<Attendee> newAttendees) {
        this.attendeeList = newAttendees;
        notifyDataSetChanged();
    }
}
