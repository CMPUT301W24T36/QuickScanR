package com.example.quickscanr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckedInAttendeeAdapter extends RecyclerView.Adapter<CheckedInAttendeeAdapter.ViewHolder> {
    private List<CheckInAttendee> attendeeList;

    public CheckedInAttendeeAdapter(List<CheckInAttendee> attendeeList) {
        this.attendeeList = attendeeList;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckInAttendee attendee = attendeeList.get(position);
        holder.nameTextView.setText(attendee.getName());
        holder.checkInCountTextView.setText(String.valueOf(attendee.getCheckInCount()));
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public void updateAttendees(List<CheckInAttendee> newAttendees) {
        attendeeList = newAttendees;
        notifyDataSetChanged();
    }
}
