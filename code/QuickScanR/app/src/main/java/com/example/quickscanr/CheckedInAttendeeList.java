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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckedInAttendeeList extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Map<String, Object>> attendeesData = new ArrayList<>();
    private CheckedInAttendeeAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checked_users_list);

        recyclerView = findViewById(R.id.chkd_usrs_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CheckedInAttendeeAdapter(attendeesData);
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

                List<Map<String, Object>> updatedAttendeesData = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Map<String, Object> attendeeData = new HashMap<>();
                    attendeeData.put("userId", doc.getId());
                    attendeeData.put("name", doc.getString("name"));
                    attendeeData.put("checkIns", doc.get("checkIns")); // Assuming "checkIns" is the field name

                    updatedAttendeesData.add(attendeeData);
                }

                attendeesData.clear();
                attendeesData.addAll(updatedAttendeesData);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
