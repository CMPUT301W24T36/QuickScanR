package com.example.quickscanr;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CheckInMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Event event;

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

        // TODO: add markers for checked in attendees
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Handler mainHandler = new Handler(Looper.getMainLooper());
        PlaceAPI placeAPI = new PlaceAPI(getApplicationContext());

        placeAPI.getPlaceLatLng("ChIJLS8LFA2sDTkRkFtTCi0awLI", mainHandler, new PlaceLatLngCallback() {
            @Override
            public void onLatLngReceived(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("awesome"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        });
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