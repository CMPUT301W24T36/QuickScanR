package com.example.quickscanr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.google.zxing.WriterException;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  This class manages displaying the QR code linking to the event's description and poster.
 *  An image of the QR code can also be shared from this page.
 */
public class PromotionQR extends InnerPageFragment {

    private String eventId;
    private long eventTimestamp;
    private static final String EVENT = "event";
    private Event event;

    private Bitmap qrCodeBitmap;

    private FirebaseFirestore db;

    public static PromotionQR newInstance(Event event) {
        PromotionQR fragment = new PromotionQR();
        Bundle args = new Bundle();
        args.putSerializable(EVENT, event); // Package the Event object for the fragment.
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(EVENT);  // Extract the Event object
        }
        if (event != null) {
            eventId = event.getId();
            eventTimestamp = event.getTimestamp();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.qr_promotional, container, false);
        ImageView qrImage = v.findViewById(R.id.qr_code);
        addButtonListeners(getActivity(), v);
        // Generate and display the initial QR code
        generateAndDisplayQRCode(qrImage, eventId, eventTimestamp);

        ImageButton shareButton = v.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareQRCode(); // Call the share method
            }
        });

        return v;
    }

    /**
     * Generates a QR code and displays it in an ImageView.
     *
     * @param qrImage ImageView where the QR code will be displayed.
     * @param eventId The unique identifier of the event.
     * @param eventTimestamp The timestamp of the event, used as part of the QR code content.
     */
    private void generateAndDisplayQRCode(ImageView qrImage, String eventId, long eventTimestamp) {
        // Combine event ID and timestamp to form the QR code content
        String qrContent = DatabaseConstants.qrTypePromo + "_" + eventId + "_0";
        try {
            // Generate the QR code and set it in the ImageView
            qrCodeBitmap = GenerateQR.generateQRCode(qrContent, 300, 300);
            qrImage.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();    // Print the stack trace if there's an error generating the QR code
        }
    }

    /**
     * Creates an image of the generated QR code and prompts Android to share it.
     */
    private void shareQRCode() {
        // Save the bitmap to a file in the cache
        String fileName = "qr_code.png";
        File cachePath = new File(getActivity().getCacheDir(), "images");
        cachePath.mkdirs(); // Make the directory
        try {
            FileOutputStream stream = new FileOutputStream(cachePath + "/" + fileName); // This overwrites any existing image
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();    // If an error occurs
        }

        // Get the URI of the file
        File imagePath = new File(getActivity().getCacheDir(), "images");
        File newFile = new File(imagePath, fileName);
        Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.example.quickscanr.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);    // Temporary permission for receiver to read this file
            shareIntent.setDataAndType(contentUri, getActivity().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Share QR code"));
        }
    }

}