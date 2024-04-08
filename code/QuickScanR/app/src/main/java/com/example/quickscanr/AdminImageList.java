package com.example.quickscanr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    ArrayList<String> posterIdList;

    ImgArrayAdapter imgArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference imgRef;
    private CollectionReference userRef;
    private CollectionReference eventRef;


    ImgHandler imgHandler;

    Button showEvents;

    Button showProfiles;

    Button allImgs;


    public static String img_COLLECTION = "images";
    public static String user_COLLECTION = "users";
    public static String event_COLLECTION = "events";




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
        userRef = db.collection(user_COLLECTION);
        eventRef = db.collection(event_COLLECTION);




        imgView = v.findViewById(R.id.view_img_list);
        imgList = new ArrayList<>();
        imgIdList = new ArrayList<>();
        posterIdList = new ArrayList<>();



        showProfiles = v.findViewById(R.id.show_prof);
        showEvents = v.findViewById(R.id.show_event);
        allImgs = v.findViewById(R.id.show_all);


        //so that the events show up
        addListeners();
        imgView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        imgHandler = new ImgHandler(getContext());

        // Assuming you have the document ID of the image
//        String documentId = "bbfAFiW1uJ2aVxZl5oit";

        addSnapshotListenerForEvent();
        AdminFragment.setNavActive(v, 2);

        //when you click events button
        showProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "hi ther eevents");
                //so that the events show up
                addListeners();
                imgView.setLayoutManager(new GridLayoutManager(getContext(), 3));

                imgHandler = new ImgHandler(getContext());

                addSnapshotListenerImgProfile();


            }
        });

        showEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "hi ther poster");
                //so that the events show up
                addListeners();
                imgView.setLayoutManager(new GridLayoutManager(getContext(), 3));

                imgHandler = new ImgHandler(getContext());

                addSnapshotListenerImgEvent();
            }

        });


        allImgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //so that the all images show up
                addListeners();
                imgView.setLayoutManager(new GridLayoutManager(getContext(), 3));

                imgHandler = new ImgHandler(getContext());

                addSnapshotListenerForEvent();
            }
        });

        return v;
    }

    //snapshot is for real time updates
    /**
     * addSnapshotListenerForEvent()
     *  - snapshot listener for the firestore database to listen to the image collection
     *  - anytime there are any changes within the database it updates
     */
    private void addSnapshotListenerForEvent() {
        imgList.clear();
        imgIdList.clear();

        imgRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG: AEL", error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }


            for (QueryDocumentSnapshot doc: value) {
                String eventName = doc.getString(DatabaseConstants.imgDataKey);
                Bitmap bitmap = ImgHandler.base64ToBitmap(eventName);

                imgList.add(bitmap);
                imgIdList.add(doc.getId());
                Log.d("DEBUG", "IMAGE Id HERE" + doc.getId());


                //Log.d("DEBUG", String.format("Img (%s) fetched: ", bitmap));
            }
            Log.d("DEBUG", "hi there" + imgList.size());
            imgArrayAdapter.notifyDataSetChanged();

        });
    }

    /**
     * addSnapshotListenerImgProfile
     * - the same as the origin addSnapshotListener but it only adds img profile pictures
     *   to the image lists
     *   - used for filtering through images
     */
    //filter profile images only
    private void addSnapshotListenerImgProfile() {

        userRef.addSnapshotListener((value, error) -> {
            posterIdList.clear();
            imgList.clear();
            imgIdList.clear();

            if (error != null) {
                Log.e("DEBUG: AEL", error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }

            for (QueryDocumentSnapshot doc: value) {
                String profileImg = doc.getString(DatabaseConstants.userImageKey);
                if(!posterIdList.contains(profileImg)){
                    posterIdList.add(profileImg);
                }


                Log.d("DEBUG", "IMAGE Id HERE" + profileImg);

            }

            for(String id : posterIdList){
                imgRef.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String eventName = documentSnapshot.getString(DatabaseConstants.imgDataKey);
                        Bitmap bitmap = ImgHandler.base64ToBitmap(eventName);

                        imgList.add(bitmap);
                        Log.d("DEBUG", "hi" + imgList.size());
                        imgIdList.add(id);
                        imgArrayAdapter.notifyDataSetChanged();

                        //Log.d("DEBUG", "key: " + eventName);
                    }

                });

            }



        });
    }

    /**
     * addSnapshotListenerImgEvent
     * - adds all event posters to image lists
     * - used for filtering through images
     */
    //filter event posters
    private void addSnapshotListenerImgEvent() {

        eventRef.addSnapshotListener((value, error) -> {
            posterIdList.clear();
            imgList.clear();
            imgIdList.clear();

            if (error != null) {
                Log.e("DEBUG: AEL", error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }

            for (QueryDocumentSnapshot doc: value) {
                String profileImg = doc.getString(DatabaseConstants.evPosterKey);
                if(!posterIdList.contains(profileImg)){
                    posterIdList.add(profileImg);
                }


                Log.d("DEBUG", "IMAGE Id HERE" + profileImg);

            }

            for(String id : posterIdList){
                Log.d("DEBUG", id + "hi there");
                imgRef.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String eventName = documentSnapshot.getString(DatabaseConstants.imgDataKey);
                        //Log.d("DEBUG", eventName + " error");

                        Bitmap bitmap = ImgHandler.base64ToBitmap(eventName);

                        imgList.add(bitmap);
                        Log.d("DEBUG", "hi" + imgList.size());
                        imgIdList.add(id);
                        imgArrayAdapter.notifyDataSetChanged();

                        //Log.d("DEBUG", "key: " + eventName);
                    }

                });

            }

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
