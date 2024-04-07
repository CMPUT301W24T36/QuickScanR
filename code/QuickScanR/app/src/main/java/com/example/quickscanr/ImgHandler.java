package com.example.quickscanr;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to upload and serve images from our firebase DB.
 * For uploads: URI -> Bitmap -> Base64
 * For downloads: Base64 -> Bitmap
 * Referenced:
 * https://stackoverflow.com/questions/9357668/how-to-store-image-in-sqlite-database
 * https://stackoverflow.com/a/65211072
 */
public class ImgHandler {
    private final FirebaseFirestore db;
    private final Context context;
    public static final String IMAGE_COLLECTION = "images";
    public static final Integer uploadLimit = 1000000;  // 1mb max on firebase
    public static final Integer uploadQuality = 80;  // slight image compression to save space

    Calendar calendar;

    /**
     * Constructor
     * @param context
     */
    public ImgHandler(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Converts and uploads an image from a given URI
     * @param imgURI Image URI provided from ImgPicker
     */
    public void uploadImage(Uri imgURI, uploadCallback callback, String uid, String name) {
        String documentID;

        // convert URI to bitmap
        Bitmap bitmap = uriToBitmap(imgURI);
        if (bitmap == null) {
            toastNotify("Failed to upload image");
            return;
        }

        // convert bitmap to base64
        String convertedImg = toBase64(bitmap);
        if (convertedImg == null || !checkImageSize(convertedImg)) {
            toastNotify("Failed to upload image");
            return;
        }


        //add current date attached
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        String currentDate = dateFormat.format(calendar.getTime());
        // upload to db
        DocumentReference imgDocRef = db.collection(IMAGE_COLLECTION).document();
        documentID = imgDocRef.getId();
        Map<String, Object> imageData = new HashMap<>();
        imageData.put(DatabaseConstants.imgDataKey, convertedImg);
        imageData.put(DatabaseConstants.imgUserKey, uid);
        imageData.put(DatabaseConstants.imgNameKey, name);
        imageData.put(DatabaseConstants.imgDateUpload, currentDate);
        imgDocRef.set(imageData)
                .addOnSuccessListener(aVoid -> toastNotify("Uploaded successfully"))
                .addOnFailureListener(e -> toastNotify("Failed to upload image"));


        // callback to provide documentID if successful
        callback.onUploadComplete(documentID);
    }

    /**
     * Retrieves image from DB based off of documentID
     * @param documentID firebase documentID
     * @param callback used to allow asynchronous DB retrieval
     */
    public void getImage(String documentID, retrievalCallback callback) {
        if (documentID == null) {
            return;
        }
        db.collection(IMAGE_COLLECTION).document(documentID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains(DatabaseConstants.imgDataKey)) {
                        String imageBase64 = documentSnapshot.getString(DatabaseConstants.imgDataKey);
                        Bitmap image = base64ToBitmap(imageBase64);
                        if (image != null) callback.onImageRetrieved(image);
                    } else {
                        toastNotify("Image failed to load");
                    }
                })
                .addOnFailureListener(e -> toastNotify("Image failed to load"));
    }

    /**
     * Given a documentID referring to an image, deletes
     * this image from the database.
     * @param documentID image document ID
     */
    public void deleteImage(String documentID) {  // NOT DONE YET
        db.collection(IMAGE_COLLECTION).document(documentID).delete();
    }

    /**
     * Simple notification box to inform users of image uploads/retrieval
     * @param message message to display to user
     */
    private void toastNotify(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Converts base64 to bitmap for display
     * @param base64Img image converted to base64
     * @return bitmap of image from db
     */
    public static Bitmap base64ToBitmap(String base64Img) {
        byte[] decodedBytes = Base64.decode(base64Img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Converts bitmap to base64 for storage
     * @param bitmap image converted to bitmap
     * @return string containing image in base64 form
     */
    private String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, uploadQuality, baOutputStream);
        byte[] byteArray = baOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Converts URI to bitmap format
     * - FROM REF 2 IN CLASS DOCUMENTATION -
     * @param URI URI of given image
     * @return Bitmap of the given URI
     */
    public Bitmap uriToBitmap(Uri URI) {
        Bitmap bitmap = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, URI);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, URI);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
        } catch (Exception ignored) {
        }
        return bitmap;
    }
    
    /**
     * Checks if image size exceeds limit
     * @param convImage Converted image
     * @return false if exceeds, true if not
     */
    public boolean checkImageSize(String convImage) {
        if (convImage.length() > uploadLimit) {
            toastNotify("Image too large");
            return false;
        }
        return true;
    }

    /**
     * Interface needed for async image retrieval.
     * Provides bitmap of image on successful retrieval.
     */
    public interface retrievalCallback {
        void onImageRetrieved(Bitmap bitmap);
    }

    /**
     * Interface needed for async image upload.
     * Provides documentID of successful upload.
     */
    public interface uploadCallback {
        void onUploadComplete(String documentId);
    }

}
