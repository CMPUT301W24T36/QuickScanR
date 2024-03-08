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

    /**
     * AdminProfileArrayAdapter:
     *
     * @param context: the state/context how the adapter is being used
     * @param users: list of profiles that is being displayed
     * @param listener : for keeping track of clicked buttons for the profiles
     */
    public AdminProfileArrayAdapter(Context context, ArrayList<User> users, AdminProfileArrayAdapter.buttonListener listener) {
        this.users = users;
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
     *  - new AdminProfileItemView(view, listener);
     *      - returning a new instance for representing an item
     */
    @NonNull
    @Override
    public AdminProfileItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_profile_item, parent, false);
        return new AdminProfileItemView(view, listener);
    }

    /**
     * onBindViewHolder
     *  - this is for populating the information inside each item (title, image, name etc)
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull AdminProfileItemView holder, int position) {
        User user = users.get(position);
        holder.profileName.setText(user.getName());
        holder.profileImage.setImageResource(R.drawable.ic_launcher_background);
    }

    /**
     * getItemCount
     *  - gets the user count
     * @return: the size of the list
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * buttonListener
     *  - interface for listening to clicked buttons
     */
    public interface buttonListener {
        /**
         * onClickButton
         *  - called when a profile is clicked
         * @param position : position of the clicked item
         */
        void onClickButton(int position);

    }
}
