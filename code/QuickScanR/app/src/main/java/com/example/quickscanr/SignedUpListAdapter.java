/**
 * manages the results shown for SignedUpList
 */

package com.example.quickscanr;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * this class acts as the array adapter for the signed up attendees list
 * manages the data of attendees and binds it to views that are displayed within a RecyclerView
 */
public class SignedUpListAdapter extends RecyclerView.Adapter<SignedUpListAdapter.ViewHolder>{
    private ArrayList<ArrayList<String>> signedUpDataList;
    private Context context;

    /**
     * constructor for initializing the adapter with a list of attendees' data
     * @param signedUpData data of signed up attendees to be displayed in the RecyclerView
     * @param context context of the view
     */
    public SignedUpListAdapter(ArrayList<ArrayList<String>> signedUpData, Context context) {
        this.signedUpDataList = signedUpData;
        this.context = context;
    }

    /**
     * called when RecyclerView needs a new ViewHolder of the given type to represent an item
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return new ViewHolder that holds a View of the given view type
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.signed_up_user_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * called by RecyclerView to display the data at the specified position
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull SignedUpListAdapter.ViewHolder holder, int position) {
        ArrayList<String> userData = signedUpDataList.get(position);

        String name = userData.get(0);
        String attendeePfp = userData.get(1);

        holder.nameTextView.setText(name);
        ImgHandler imgHandler = new ImgHandler(context);
        imgHandler.getImage(attendeePfp, holder.profilePictureView::setImageBitmap);
    }

    /**
     * returns the total number of items in the data set held by the adapter
     * @return number of items in signedUpDataList
     */
    @Override
    public int getItemCount() {
        return signedUpDataList.size();
    }

    /**
     * ViewHolder describes an item view and metadata about its place within the RecyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView profilePictureView;

        /**
         * constructor for the ViewHolder - used to find and store views (for faster access)
         * @param view : view held by the ViewHolder
         */

        ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.signed_up_attendee_name);
            profilePictureView = view.findViewById(R.id.signed_up_attendee_pfp);
        }
    }
}
