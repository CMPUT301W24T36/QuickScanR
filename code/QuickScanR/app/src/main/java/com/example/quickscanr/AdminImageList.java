package com.example.quickscanr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Admin Image List:
 * - allows admin to view all the images and click on each one to view the image,
 *   who uploaded it, and the date posted
 */
public class AdminImageList extends AdminFragment{


    RecyclerView imgView;

    ArrayList<Bitmap> imgList;
    ArrayList<String> imgIdList;
    ImgArrayAdapter imgArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference imgRef;
    ImgHandler imgHandler;

    public static String img_COLLECTION = "images";


    public AdminImageList() {}

    /**
     * AdminImageList
     *  - creates a new instance of the AdminImageList fragment
     * @param param1
     *      -
     * @param param2
     * @return
     *  - returns fragment: which is of the new instance AdminImageList
     */
    public static AdminImageList newInstance(String param1, String param2) {
        AdminImageList fragment = new AdminImageList();
        return fragment;
    }

    /**
     * OnCreate
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onCreateView
     *  - creates the view and inflates layout so that the
     *      browse image list can be displayed
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *      - returns v, which is the view with the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_browse_images, container, false);
        addNavBarListeners(getActivity(), v);
        Log.d("DEBUG", "hi");


//         DB LINKING
        db = FirebaseFirestore.getInstance();

        imgRef = db.collection(img_COLLECTION);

        imgView = v.findViewById(R.id.view_img_list);
        imgList = new ArrayList<>();
        imgIdList = new ArrayList<>();


        //so that the events show up
        addListeners();
        imgView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        imgHandler = new ImgHandler(getContext());

        // Assuming you have the document ID of the image
//        String documentId = "bbfAFiW1uJ2aVxZl5oit";

        addSnapshotListenerForEvent();

        return v;
    }

    //snapshot is for real time updates
    /**
     * addSnapshotListenerForEvent()
     *  - snapshot listener for the firestore database to listen to the image collection
     *  - anytime there are any changes within the database it updates
     */
    private void addSnapshotListenerForEvent() {
        imgRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG: AEL", error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }

            imgList.clear();
            for (QueryDocumentSnapshot doc: value) {
                String eventName = doc.getString(DatabaseConstants.imgDataKey);
                Bitmap bitmap = ImgHandler.base64ToBitmap(eventName);
                String imgid = doc.getString(DatabaseConstants.imgId);

                imgList.add(bitmap);
                imgIdList.add(imgid);
                Log.d("DEBUG", "IMAGE Id HERE" + imgid);


                Log.d("DEBUG", String.format("Img (%s) fetched: ", bitmap));
            }
            imgArrayAdapter.notifyDataSetChanged();

        });
    }

    //listen for the clickable items
    /**
     * addListeners:
     *  - create eventArrayAdapter and set it as the adapter for the recycler view
     *  - keeps track of position when event is clicked
     */
    public void addListeners() {
        imgArrayAdapter = new ImgArrayAdapter(getContext(), imgList, imgIdList, (position, imageId) -> buttonClickAction(imgList.get(position), imgIdList.get(position)));
        imgView.setAdapter(imgArrayAdapter);
    }

    /**
     * buttonClickAction:
     *  - when image is clicked, it will send specific data to AdminManageImage page
     *  - includes ability to move forward and backwards to different pages
     */

    private void buttonClickAction(Bitmap bitmap, String imageId) {
        //When you click on the buttonClickAction, it will link the position and take you
        //to the manage image that fills in more info

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, AdminManageImage.newInstance(bitmap, imageId))
                .addToBackStack(null).commit();
    }
}
