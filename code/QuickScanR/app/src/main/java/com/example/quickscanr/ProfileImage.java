package com.example.quickscanr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class handles the creation and retrieval of default profile images.
 * It provides methods to generate profile images based on user data and retrieve profile images from Firebase Firestore.
 */

public class ProfileImage {

    private Context context;

    /**
     * Constructor to initialize the context.
     * @param context The application context
     */
    public ProfileImage(Context context) {
        this.context = context;
    }

    /**
     * Callback interface to notify when the profile image is ready.
     */
    public interface ProfileImageCallback {
        void onImageReady(Bitmap image);
    }

    /**
     * Retrieves the profile image for the given user ID.
     * If no image is found, a default profile image is created.
     * @param context The application context
     * @param userId The ID of the user
     * @param callback The callback to handle the profile image retrieval
     */
    public static void getProfileImage(Context context, String userId, ProfileImageCallback callback) {
        // Method to retrieve the profile image for the given user ID
        if (userId == null || userId.trim().isEmpty()) {
            Log.e("ProfileImage", "Provided userId is null or empty");
            Bitmap defaultImage = createProfileImage(context, "??", 200, 200);
            callback.onImageReady(defaultImage);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            String imageStatus = documentSnapshot.getString("image");
            if ("default_user".equals(imageStatus)) {
                //if user has not uploaded a profile picture
                String userName = documentSnapshot.getString("name");
                Bitmap profileImage = createProfileImage(context, userName, 200, 200);
                callback.onImageReady(profileImage);
            } else {
                //if user has already uploaded a profile picture
                ImgHandler imgHandler = new ImgHandler(context);
                imgHandler.getImage(imageStatus, new ImgHandler.retrievalCallback() {
                    @Override
                    public void onImageRetrieved(Bitmap bitmap) {
                        callback.onImageReady(bitmap);
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Bitmap errorImage = createProfileImage(context, "??", 200, 200);
            callback.onImageReady(errorImage);
        });
    }

    /**
     * Creates a default profile image with the user's initials.
     * @param context The application context
     * @param name The name of the user
     * @param width The width of the profile image
     * @param height The height of the profile image
     * @return The generated profile image bitmap
     */
    public static Bitmap createProfileImage(Context context, String name, int width, int height) {
        // Method to create a default profile image with the user's initials
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        int backgroundColor = ContextCompat.getColor(context, R.color.light_green);
        int textColor = ContextCompat.getColor(context, R.color.black);

        canvas.drawColor(backgroundColor);

        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(50);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Paint.Align.CENTER);

        String initials = getInitials(name);

        float xPos = width / 2f;
        float yPos = (height / 2f) - ((paint.descent() + paint.ascent()) / 2);

        canvas.drawText(initials, xPos, yPos, paint);

        return image;
    }

    /**
     * Retrieves the initials from the user's name to generate the profile image.
     * @param name The name of the user
     * @return The initials extracted from the name
     */
    private static String getInitials(String name) {
        // Method to extract initials from the user's name
        if (name == null || name.trim().isEmpty()) {
            return "x";
        }

        String[] words = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }
}
