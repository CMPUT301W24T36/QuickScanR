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

public class CheckedInAttendeeAdapter extends RecyclerView.Adapter<CheckedInAttendeeAdapter.ViewHolder> {
    private List<Map<String, Object>> attendeeDataList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public CheckedInAttendeeAdapter(List<Map<String, Object>> attendeeDataList) {
        this.attendeeDataList = attendeeDataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_user_item, parent, false);
        return new ViewHolder(view);
    }

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
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendeeDataList.size();
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
}