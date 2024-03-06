package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.Nullable;

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

    private TextView attendeeCountTextView;

    private CheckedInAttendeeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checked_users_list);

        RecyclerView attendeesRecyclerView = findViewById(R.id.chkd_usrs_list);

        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CheckedInAttendeeAdapter(this, new ArrayList<>());
        attendeesRecyclerView.setAdapter(adapter);

        String eventId = "event_id_here";
        listenForAttendeeUpdates(eventId);
    }

    private void listenForAttendeeUpdates(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(eventId).collection("attendees")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("AttendeeList", "Listen failed.", e);
                            return;
                        }

                        List<User> users = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots) {
                            User user = doc.toObject(User.class);
                            users.add(user);
                        }
                        adapter.setUserList(users);

                        // calculates the total count of checked-in attendees
                        int checkedInCount = users.size();
                        // updates the count TextView
                        if (attendeeCountTextView != null) {
                            attendeeCountTextView.setText(String.valueOf(checkedInCount));
                        }
                    }
                });
    }

}
