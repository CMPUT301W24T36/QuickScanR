/**
 * populates the browse image page recycler view for admins
 */

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

    /**
     *
     * @param context
     * @param images - list of images collected in the browse
     * @param ids - list of image document id's
     * @param listener
     */
    public ImgArrayAdapter(Context context, ArrayList<Bitmap> images, ArrayList<String> ids, ImgArrayAdapter.buttonListener listener) {
        this.images = images;
        this.ids = ids;
        this.context = context;
        this.listener = listener;
    }

    /**
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     *      - the new item for all the images in the lsit
     */
    @NonNull
    @Override
    public ImgItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adm_img_item, parent, false);
        return new ImgItemView(view, listener, ids);
    }

    /**
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ImgItemView holder, int position) {
        Bitmap image = images.get(position);
        String id = ids.get(position);
        holder.image.setImageBitmap(image);
    }

    /**
     *
     * @return : the size of the image list
     */
    public int getItemCount() {
        return images.size();
    }

    /**
     * button Listener
     * - gathering the position and imageid needed
     */
    public interface buttonListener {
        /**
         *
         * @param position: position of the clicked item
         * @param imageId: same position as the image
         */
        void onClickButton(int position, String imageId);

    }
}
