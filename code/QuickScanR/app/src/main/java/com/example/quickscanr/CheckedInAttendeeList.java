package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckedInAttendeeList extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<CheckInAttendee> attendeesList = new ArrayList<>();
    private CheckedInAttendeeAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checked_users_list);

        recyclerView = findViewById(R.id.chkd_usrs_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CheckedInAttendeeAdapter(attendeesList);
        recyclerView.setAdapter(adapter);

        setupFirestoreRealtimeUpdate();
    }

    private void setupFirestoreRealtimeUpdate() {
        // replace 'YOUR_EVENT_ID' with the actual event ID
        final CollectionReference attendeesRef = db.collection("events").document("YOUR_EVENT_ID").collection("attendees");

        attendeesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("CheckedInAttendeeList", "Listen failed.", e);
                    return;
                }

                List<CheckInAttendee> updatedAttendees = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    String userId = doc.getId();
                    String name = doc.getString("name");
                    // Assume that 'checkIns' is a List of Timestamps representing each check-in
                    List<Timestamp> checkIns = (List<Timestamp>) doc.get("checkIns");
                    int checkInCount = (checkIns != null) ? checkIns.size() : 0;

                    updatedAttendees.add(new CheckInAttendee(userId, name, checkInCount));
                }

                // Update the list and the adapter
                attendeesList = updatedAttendees;
                adapter.updateAttendees(updatedAttendees);
            }
        });
    }
}
