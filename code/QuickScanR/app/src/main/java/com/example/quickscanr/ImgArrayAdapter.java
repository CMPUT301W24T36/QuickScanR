package com.example.quickscanr;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * ImgArrayAdapter
 * - - this class is for populating the recycler view with different images
 *  * - the methods are for inflating views, binding positions and getting the size of img list
 */
public class ImgArrayAdapter extends RecyclerView.Adapter<ImgItemView> {
    private final ArrayList<Bitmap> images;
    private final ArrayList<String> ids;
    private final Context context;
    private final ImgArrayAdapter.buttonListener listener;

    public ImgArrayAdapter(Context context, ArrayList<Bitmap> images, ArrayList<String> ids, ImgArrayAdapter.buttonListener listener) {
        this.images = images;
        this.ids = ids;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImgItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adm_img_item, parent, false);
        return new ImgItemView(view, listener, ids);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgItemView holder, int position) {
        Bitmap image = images.get(position);
        String id = ids.get(position);
        holder.image.setImageBitmap(image);
    }

    public int getItemCount() {
        return images.size();
    }

    public interface buttonListener {
        /**
         *
         * @param position: position of the clicked item
         * @param imageId: same position as the image
         */
        void onClickButton(int position, String imageId);

    }
}
