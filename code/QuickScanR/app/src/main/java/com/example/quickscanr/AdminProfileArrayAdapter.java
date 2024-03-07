package com.example.quickscanr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * AdminProfileArrayAdapter
 * - this class is for populating the recycler view with different profiles
 * - the methods are for ifnlating views, binding positions and getting the size of user list
 */
public class AdminProfileArrayAdapter extends RecyclerView.Adapter<AdminProfileItemView>{
    private final ArrayList<User> users;
    private final Context context;
    private final AdminProfileArrayAdapter.buttonListener listener;

    public AdminProfileArrayAdapter(Context context, ArrayList<User> users, AdminProfileArrayAdapter.buttonListener listener) {
        this.users = users;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminProfileItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_profile_item, parent, false);
        return new AdminProfileItemView(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProfileItemView holder, int position) {
        User user = users.get(position);
        holder.profileName.setText(user.getName());
        holder.profileImage.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface buttonListener {
        void onClickButton(int position);

    }
}
