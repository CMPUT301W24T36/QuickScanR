/**
 * This file deals with the QR Scanning
 */
package com.example.quickscanr;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.quickscanr.AttendeeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * QR Scanner Class
 * Contains all logic for scanner page. Initializes a qr/barcode
 * scanner in the middle of the screen and constantly scans for input.
 * Validation, DB request for scanned event and transitions handled.
 */
public class ScanQR extends AttendeeFragment {

    /**
     * Constructor
     */
    public ScanQR() {}

    // Variables
    private DecoratedBarcodeView scanView;
    private FirebaseFirestore db;
    public static String EVENT_COLLECTION = "events";
    private String lastScan;
    private static ScanQR instance;

    /**
     *  Creates a newInstance for scan QR
     * @param param1 a String, default for newInstance
     * @param param2 a String, default for newInstance
     * @return
     */
    public static ScanQR newInstance(String param1, String param2) {
        ScanQR fragment = new ScanQR();
        instance = fragment;
        return fragment;
    }

    /**
     * Called for the initial creation of the fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View to be created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.qr_scanning,container,false);
        addNavBarListeners(getActivity(), v);
        db = FirebaseFirestore.getInstance();
        initScanner(v);
        AttendeeFragment.setNavActive(v, 2);
        return v;
    }

    /**
     * Returns current instance of ScanQR. Needed for testing.
     */
    public ScanQR getInstance() {
        return instance;
    }

    /**
     * Starts scanner on screen and sets its status to scanning.
     */
    public void initScanner(View v) {
        scanView = v.findViewById(R.id.scanner);
        scanView.initializeFromIntent(getActivity().getIntent());
        scanView.setStatusText("Scanning...");
        scanView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult data) {
                if(data.getText() == null || data.getText().equals(lastScan)) {
                    return;
                }

                lastScan = data.getText();
                onScan(String.valueOf(data));
            }
        });
    }

    /**
     * Code to execute on scanner resume
     */
    @Override
    public void onResume() {
        super.onResume();
        scanView.resume();
    }

    /**
     * Code to execute on scanner pause
     */
    @Override
    public void onPause() {
        super.onPause();
        scanView.pause();
    }

    /**
     * Data retrieved from scanner is sent here as a string
     * and actions are performed on it.
     * @param data Data from latest scan
     */
    public void onScan(String data) {
        if (!checkQR(data)) return;  // validity check
        String[] qrInfo = parseQRData(data);
        String type = qrInfo[0];
        String eventID = qrInfo[1];
        String eventTimestamp = qrInfo[2];

        // call respective methods
        if (Objects.equals(type, DatabaseConstants.qrTypeCheckIn)) {
            checkIn(eventID);
        } else if (Objects.equals(type, DatabaseConstants.qrTypePromo)) {
            viewPromo(eventID);
        } else {
            Log.d("DEBUG", "QRScan has invalid type other than CI,PR");
        }
    }

    /**
     * Modular parsing for QR code data.
     * @param data Data from latest scan
     * @return Str array: qrtype, qrevent, qrtimestamp
     */
    private String[] parseQRData(String data) {
        String[] arrSplitData = data.split("_");
        String qrType = arrSplitData[0];
        String qrEvent = arrSplitData[1];
        String qrTimestamp = arrSplitData[2];

        return new String[]{qrType, qrEvent, qrTimestamp};
    }

    /**
     * Performs needed checks before onScan continues
     * with actions related to the barcode.
     * @param data Data from latest scan
     * @return boolean: true if valid, false if not
     */
    private boolean checkQR(String data) {
        // TO BE ADDED: need more robust checks
        String v1 = DatabaseConstants.qrTypeCheckIn;
        String v2 = DatabaseConstants.qrTypePromo;
        return data.startsWith(v1) || data.startsWith(v2);
    }

    /**
     * Scanning Case: Check-in QR code scanned
     * @param eventID ID of event
     */
    private void checkIn(String eventID) {
        db.collection(EVENT_COLLECTION).document(eventID).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Bundle args = new Bundle();
                        Event newEvent = buildEvent(doc);
                        String eventPosterID = doc.getString(DatabaseConstants.evPosterKey);

                        db.collection(DatabaseConstants.usersColName).document(doc.getString(DatabaseConstants.evOwnerKey)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        User organizer = doc.toObject(User.class);
                                        organizer.setUserId(doc.getId());
                                        newEvent.setOrganizer(organizer);
                                        EventDetails evDetFragment = EventDetails.newInstance(newEvent);
                                        args.putBoolean("showCheckInDialog", true);
                                        args.putSerializable("event", newEvent);
                                        evDetFragment.setArguments(args);


                                        if (!Objects.equals(eventPosterID, "")) {
                                            ImgHandler imgHandler = new ImgHandler(getContext());
                                            imgHandler.getImage(eventPosterID, bitmap -> {
                                                newEvent.setPoster(bitmap);

                                                // transition to event after async grab
                                                getActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.content_main, evDetFragment, "CI_EVENT")
                                                        .addToBackStack(null).commit();
                                            });
                                        } else {
                                            // transition to event
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.content_main, evDetFragment, "CI_EVENT")
                                                    .addToBackStack(null).commit();
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        Log.d("DEBUG", "Attempted to retrieve event that did not exist");
                    }
                })
                .addOnFailureListener(e -> Log.d("DEBUG", "Failed to grab event: %e", e));
    }

    /**
     * Scanning Case: Promotional QR code scanned
     * @param eventID ID of event
     */
    private void viewPromo(String eventID) {
        db.collection(EVENT_COLLECTION).document(eventID).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Event newEvent = buildEvent(doc);
                        String eventPosterID = doc.getString(DatabaseConstants.evPosterKey);

                        // get user data + imgs
                        db.collection(DatabaseConstants.usersColName).document(doc.getString(DatabaseConstants.evOwnerKey)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        User organizer = doc.toObject(User.class);
                                        organizer.setUserId(doc.getId());
                                        newEvent.setOrganizer(organizer);
                                        EventDetails evDetFragment = EventDetails.newInstance(newEvent);

                                        if (!Objects.equals(eventPosterID, "")) {
                                            ImgHandler imgHandler = new ImgHandler(getContext());
                                            imgHandler.getImage(eventPosterID, bitmap -> {
                                                newEvent.setPoster(bitmap);
                                                showSignUpOrNot(eventID, evDetFragment, newEvent);
                                            });
                                        } else {
                                            showSignUpOrNot(eventID, evDetFragment, newEvent);
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        Log.d("DEBUG", "Attempted to retrieve event that did not exist");
                    }
                })
                .addOnFailureListener(e -> Log.d("DEBUG", "Failed to grab event: %e", e));
    }

    /**
     * shows the sign up button on the event details page if the user is not signed up or checked in for the event
     * @param eventID id of the event to show details page for
     * @param evDetFragment event details fragment for the event
     * @param newEvent event object with event data
     */
    public void showSignUpOrNot(String eventID, EventDetails evDetFragment, Event newEvent) {
        // show sign up button for event if user is not already signed up or checked in
        db.collection(DatabaseConstants.usersColName).document(MainActivity.user.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDoc = task.getResult();
                    if (userDoc.exists()) {
                        ArrayList<String> checkedEvents = (ArrayList<String>) userDoc.get(DatabaseConstants.userCheckedEventsKey);
                        ArrayList<String> signedUpEvents = (ArrayList<String>) userDoc.get(DatabaseConstants.userSignedUpEventsKey);
                        boolean showSignUp = false;
                        if (!checkedEvents.contains(eventID) && !signedUpEvents.contains(eventID)) {
                            showSignUp = true;
                        }
                        Bundle args = new Bundle();
                        args.putSerializable("event", newEvent);
                        args.putBoolean("showSignUpDialog", showSignUp);
                        evDetFragment.setArguments(args);

                        // transition to event after async grab
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_main, evDetFragment, "PR_EVENT")
                                .addToBackStack(null).commit();
                    }
                }
            }
        });
    }

    /**
     * Class to build a new event object from DB data
     * !!! Note: This class will be removed after halfway point,
     * AttEvList should provide all events just currently
     * unsure if it updates when fragment is not displayed.
     */
    private Event buildEvent(DocumentSnapshot doc) {
        String eventName = doc.getString(DatabaseConstants.evNameKey);
        String eventDesc = doc.getString(DatabaseConstants.evDescKey);
        String eventLocName = doc.getString(DatabaseConstants.evLocNameKey);
        String eventLocId = doc.getString(DatabaseConstants.evLocIdKey);
        String eventRest = doc.getString(DatabaseConstants.evRestricKey);
        String eventStart = doc.getString(DatabaseConstants.evStartKey);
        String eventEnd = doc.getString(DatabaseConstants.evEndKey);
        String eventID = doc.getId();

        User orgTemp = new User("Loading","Loading","Loading",0);
        Event newEvent = new Event(eventName, eventDesc, eventLocName, eventLocId, eventStart, eventEnd, eventRest, orgTemp);
        newEvent.setId(eventID);
        Log.d("DEBUG", String.format("Event (%s) fetched", eventName));
        return newEvent;
    }
}