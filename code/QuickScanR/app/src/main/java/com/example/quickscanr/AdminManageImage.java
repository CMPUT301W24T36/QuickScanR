package com.example.quickscanr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

/**
 * AdminManageImage
 * - allows admin to view more info about the image and who uploaded it
 */
public class AdminManageImage extends InnerPageFragment{

    private Bitmap bitmap;

    private String img_id;
    private String username;
    private ImgHandler imgHandler;

    private TextView profileName;
    private TextView uploadDate;


    Button deleteImages;

    private FirebaseFirestore db;
    private CollectionReference imgRef;

    private CollectionReference usersRef;

    public static String IMAGE_COLLECTION = "images";
    public static String USER_COLLECTION = "users";


    public AdminManageImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    //On create we need to inflate the xml and create the fragment that can be connected to the image page
    /**
     * onCreate:
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bitmap = (Bitmap) getArguments().getParcelable("image");
            img_id = getArguments().getString("imageId");
        }

    }

    /**
     * onCreateView:
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *       - returns v, which is the view with the inflated layout
     *       - also returns the updated version of any change made with deleting
     *       - user who uploaded the image is displayed
     *       - date that image was uploaded is also displayed
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_manage_image, container, false);
        //go back to events list when clicked
        addButtonListeners(getActivity(), v, new AdminImageList());

        deleteImages = v.findViewById(R.id.delete_img);

        profileName = v.findViewById(R.id.img_name);

        uploadDate = v.findViewById(R.id.img_date);



        //set up the database
        db = FirebaseFirestore.getInstance();
        imgRef = db.collection(IMAGE_COLLECTION);

        usersRef = db.collection(USER_COLLECTION);

        populateInfo(v);


        deleteImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgRef.document(img_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //go back to the previous page
                                    AdminImageList adminImageList = new AdminImageList();
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_main, adminImageList)
                                            .addToBackStack(null).commit();
                    }
                });


            }
        });


        return v;
    }

    /**
     *
     * @param bitmap: specific data regarding image
     * @param imageId: the imageid of the picture clicked
     * @return
     */
    public static AdminManageImage newInstance(Bitmap bitmap, String imageId) {
        AdminManageImage fragment = new AdminManageImage(bitmap);
        Bundle args = new Bundle();

        args.putParcelable("image", bitmap);
        args.putString("imageId", imageId);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     *populateInfo:
     *  - populate image, info about profile, date
     * @param v: view that has the fields with the same data that we will populate the fields with
     */
    public void populateInfo(View v){
        getUserId(img_id);

        ImageView img = v.findViewById(R.id.img_pic);
        img.setImageBitmap(bitmap);

    }

    /**
     * getUserId()
     * @param img_id : document id of the image
     *  - queries the database through the image document to collect data to
     *    populate the username and date upload
     */
    public void getUserId(String img_id){

        imgRef.document(img_id).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                String username = documentSnapshot.getString(DatabaseConstants.imgNameKey);
                String date = documentSnapshot.getString(DatabaseConstants.imgDateUpload);
                profileName.setText(username);
                uploadDate.setText(date);
            }
        });
    }
}
