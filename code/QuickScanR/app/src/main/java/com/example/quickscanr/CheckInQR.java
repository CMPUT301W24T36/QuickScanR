package com.example.quickscanr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.zxing.WriterException;

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

            // show confirmation dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("New Check-in QR code generated!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
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
            Bitmap qrCodeBitmap = GenerateQR.generateQRCode(qrContent, 300, 300);
            qrImage.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();    // Print the stack trace if there's an error generating the QR code
        }
    }
}