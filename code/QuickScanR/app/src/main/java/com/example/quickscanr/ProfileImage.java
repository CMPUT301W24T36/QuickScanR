package com.example.quickscanr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileImage {

    private Context context;

    public ProfileImage(Context context) {
        this.context = context;
    }

    public interface ProfileImageCallback {
        void onImageReady(Bitmap image);
    }

    public static void getProfileImage(Context context, String userId, ProfileImageCallback callback) {
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
                String userName = documentSnapshot.getString("name");
                Bitmap profileImage = createProfileImage(context, userName, 200, 200);
                callback.onImageReady(profileImage);
            } else {
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
    public static Bitmap createProfileImage(Context context, String name, int width, int height) {
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

    private static String getInitials(String name) {
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
