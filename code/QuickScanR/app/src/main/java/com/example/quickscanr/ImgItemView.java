package com.example.quickscanr;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * ImgItemView
 * - this is for what is is being displayed for each image that is listed
 * - also for capturing the position when the item is clicked on
 */
public class ImgItemView extends RecyclerView.ViewHolder {

    public ImageView image;
    private final ArrayList<String> ids;



    /**
     * AdminEventItemView:
     *  - represents an item in the recycler view and defines what elements need
     *      to be displayed in the view
     *
     * @param itemView : the view for what is displayed on the layout (name, desc, image, etc)
     * @param listener : listens to when button is clicked and gets position
     */
    public ImgItemView(View itemView, ImgArrayAdapter.buttonListener listener, ArrayList<String> ids) {
        super(itemView);
        this.ids = ids;
        image = itemView.findViewById(R.id.evt_img);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                String imageId = getImgId(position);
                Log.d("button", "onClick:" + getAdapterPosition());
                listener.onClickButton(position, imageId);
            }
        });

    }

    private String getImgId(int position){
        if(position < ids.size()){
            return ids.get(position);
        }
        return null;
    }
}
