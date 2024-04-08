package com.example.quickscanr;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * AdminProfileItemView
 * - this is for what is is being displayed for each profile that is listed
 * - also for capturing the position when the item is clicked on
 */
public class AdminProfileItemView extends RecyclerView.ViewHolder {

    public TextView profileName;
    public ImageView profileImage;
    private final ArrayList<String> ids;


    ImageButton editEvent;


    /**
     * AdminProfileItemView:
     *  - represents an item in the recycler view and defines what elements need
     *      to be displayed in the view
     *
     * @param itemView : the view for what is displayed on the layout (name, images, etc)
     * @param listener : listens to when button is clicked and gets position
     */
    public AdminProfileItemView(View itemView, AdminProfileArrayAdapter.buttonListener listener, ArrayList<String> ids) {
        super(itemView);
        this.ids = ids;

        //this is whats being displayed on the screen
        profileName = itemView.findViewById(R.id.adm_prf_name);
        profileImage = itemView.findViewById(R.id.adm_prf_pic);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                String userId = getUserId(position);

                Log.d("button", "onClick:" + getAdapterPosition());
                listener.onClickButton(position, userId);
            }
        });

//
    }

    /**
     * getUserID
     * @param position
     * @return
     *  - position of the userid in the list
     */
    private String getUserId(int position){
        if(position < ids.size()){
            return ids.get(position);
        }
        return null;
    }

}
