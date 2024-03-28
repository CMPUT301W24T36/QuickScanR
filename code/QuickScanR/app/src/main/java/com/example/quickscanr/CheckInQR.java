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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

/**
 * This class manages displaying and generating QR codes for event check-ins.
 * A new QR code is generated using the current time in ms to make sure it's unique.
 */
public class CheckInQR extends InnerPageFragment {
    private String eventId;
    private long eventTimestamp;
    private static final String EVENT = "event";
    private Event event;

    private FirebaseFirestore db;

    private Bitmap qrCodeBitmap;

    public static CheckInQR newInstance(Event event) {
        CheckInQR fragment = new CheckInQR();
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
        View v = inflater.inflate(R.layout.qr_check_in, container, false);
        ImageView qrImage = v.findViewById(R.id.qr_code);
        addButtonListeners(getActivity(), v, EventDashboard.newInstance(event));
        // Generate and display the initial QR code
        generateAndDisplayQRCode(qrImage, eventId, eventTimestamp);

        // Set up a listener for the button to generate a new QR code
        v.findViewById(R.id.generate_new_qr).setOnClickListener(view -> {
            // Update the timestamp to the current system time
            eventTimestamp = System.currentTimeMillis();
            if (event != null) {
                event.setTimestamp(eventTimestamp);
                // Generate and display a new QR code with updated timestamp
                generateAndDisplayQRCode(qrImage, eventId, eventTimestamp);
                // Update event timestamp in Firebase
                DocumentReference eventRef = db.collection("events").document(event.getId());
                Map<String, Object> updates = new HashMap<>();
                updates.put("timestamp", event.getTimestamp());
                eventRef.update(updates);
            }
        });

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
        String qrContent = DatabaseConstants.qrTypeCheckIn + "_" + eventId + "_" + eventTimestamp;
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