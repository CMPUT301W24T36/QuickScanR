package com.example.quickscanr;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CheckInMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Event event;

    private FirebaseFirestore db;
    private PlaceAPI placeAPI;
    private Handler mainHandler;

    /**
     * Called when creating the activity CheckInMap
     * @param savedInstanceState If the activity is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.check_in_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("event");

        setupAdditionalListeners();

        db = FirebaseFirestore.getInstance();

        mainHandler = new Handler(Looper.getMainLooper());
        placeAPI = new PlaceAPI(getApplicationContext());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Event event = (Event) getIntent().getSerializableExtra("event");
        String eventLocationID = event.getLocationId();
        String eventLocationName = "EVENT: " + event.getName();
        placePin(eventLocationID, eventLocationName);

        // After placing the pin, move and zoom the map to the event's location
        placeAPI.getPlaceLatLng(eventLocationID, mainHandler, new PlaceLatLngCallback() {
            @Override
            public void onLatLngReceived(LatLng latLng) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        });

        fetchAndDisplayAttendeeLocations();
    }

    /**
     * This fetches attendee locations and calls placePin to put their pins on the map
     */
    private void fetchAndDisplayAttendeeLocations() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Event event = (Event) getIntent().getSerializableExtra("event");
        CollectionReference attendeesRef = db.collection(DatabaseConstants.eventColName).document(event.getId()).collection("attendees");

        attendeesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot attendeeDocument : task.getResult()) {
                    // Check if locationID exists and is not "no location"
                    String locationID = attendeeDocument.getString("locationID");
                    placePin(locationID, "");   // no name
                }
            } else {
                Log.w("CheckInMap", "Error getting attendee documents: ", task.getException());
            }
        });
    }

    /**
     * This method places a pin on the map using its unique ID and a name to label it (which can
     * be left null for attendees)
     * @param locationID the unique ID of the location
     * @param locationName the name of the location
     * a previous saved state, this is the state.
     */
    private void placePin(String locationID, String locationName) {
        if (locationID != null && !locationID.equals("no location")) {
            // Fetch the location using the PlaceAPI and place a pin on the map
            placeAPI.getPlaceLatLng(locationID, mainHandler, new PlaceLatLngCallback() {
                @Override
                public void onLatLngReceived(LatLng latLng) {
                    // Place a pin on the map
                    mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                }
            });
        }
    }


    /**
     * This sets up listeners for UI buttons
     */
    public void setupAdditionalListeners() {
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CheckInMap.this, MainActivity.class);
                myIntent.putExtra("backFromCheckInMap", true);
                myIntent.putExtra("event", event);
                CheckInMap.this.startActivity(myIntent);
                finish();
            }
        });
    }

}