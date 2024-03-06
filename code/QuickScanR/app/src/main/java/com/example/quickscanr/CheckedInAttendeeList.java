package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class CheckedInAttendeeListActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Attendee> attendeesList = new ArrayList<>();
    private CheckedInAttendeeAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checked_users_list); // Make sure this layout exists

        recyclerView = findViewById(R.id.chkd_usrs_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CheckedInAttendeeAdapter(attendeesList);
        recyclerView.setAdapter(adapter);

        setupFirestoreRealtimeUpdate();
    }

    private void setupFirestoreRealtimeUpdate() {
        final CollectionReference attendeesRef = db.collection("events").document("YOUR_EVENT_ID").collection("attendees");

        attendeesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("CheckedInAttendeeList", "Listen failed.", e);
                    return;
                }

                List<Attendee> updatedAttendees = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    String userId = doc.getId();
                    String name = doc.getString("name");
                    List<Timestamp> checkIns = (List<Timestamp>) doc.get("checkIns"); // Assuming "checkIns" is the field name

                    updatedAttendees.add(new Attendee(userId, name, checkIns));
                }

                adapter.updateAttendees(updatedAttendees);
            }
        });
    }
}
