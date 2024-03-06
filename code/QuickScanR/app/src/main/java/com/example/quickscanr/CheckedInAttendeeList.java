package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckedInAttendeeList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CheckedInAttendeeAdapter adapter;
    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checked_users_list); // Make sure you have this layout.

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("EVENT_ID"); // Assumes EVENT_ID is passed to this activity.

        recyclerView = findViewById(R.id.chkd_usrs_list); // Ensure you have a RecyclerView with this ID in your layout.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with empty lists until data is fetched
        adapter = new CheckedInAttendeeAdapter(this, new ArrayList<>(), new HashMap<>());
        recyclerView.setAdapter(adapter);

        fetchEventAndAttendees();
    }

    private void fetchEventAndAttendees() {
        DocumentReference eventRef = db.collection(DatabaseConstants.eventColName).document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        // Assuming Event class has a method to get a list of User IDs and a separate method to get checkedInCounts by User ID
                        List<User> attendees = event.getAttendees(); // This should be adjusted based on your actual data structure.
                        HashMap<String, Integer> checkedInCounts = new HashMap<>(); // Adjust this to fetch actual check-in counts for each user.
                        for (User attendee : attendees) {
                            // Assuming each User object contains a unique identifier (userID).
                            checkedInCounts.put(attendee.getEmail(), event.getCheckedInCounts().get(attendee)); // Adjust based on your data structure.
                        }
                        // Now update the UI with the fetched data
                        updateUI(attendees, checkedInCounts);
                    }
                } else {
                    Log.d("CheckedInAttendeesList", "No such document");
                }
            } else {
                Log.d("CheckedInAttendeesList", "get failed with ", task.getException());
            }
        });
    }

    private void updateUI(@NonNull List<User> attendees, @NonNull HashMap<String, Integer> checkedInCounts) {
        adapter.updateData(attendees, checkedInCounts);
    }
}
