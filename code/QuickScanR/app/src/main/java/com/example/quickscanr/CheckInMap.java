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
        event = (Event) getIntent().getSerializableExtra("event");
        String eventLocationID = event.getLocationId();
        placeAPI.getPlaceLatLng(eventLocationID, mainHandler, new PlaceLatLngCallback() {
            @Override
            public void onLatLngReceived(LatLng latLng) {
                placePin(latLng.latitude, latLng.longitude, "EVENT: " + event.getName());
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
                    Boolean geoLoc = attendeeDocument.getBoolean("geoLoc"); // Get the geoLoc value
                    if (geoLoc != null && geoLoc) { // Check if geoLoc is true
                        double latitude = attendeeDocument.getDouble("latitude");
                        double longitude = attendeeDocument.getDouble("longitude");
                        placePin(latitude, longitude, ""); // No name for attendees
                    }
                }
            } else {
                Log.w("CheckInMap", "Error getting attendee documents: ", task.getException());
            }
        });
    }

    /**
     * This method places a pin on the map using its long and lat and a name to label it)
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     * @param locationName the name of the location
     * a previous saved state, this is the state.
     */
    private void placePin(double latitude, double longitude, String locationName) {
        LatLng latLng = new LatLng(latitude, longitude);
        Log.d("MARKER ADDED", "Lat: " + latitude + ", Long: " + longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
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