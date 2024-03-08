package com.example.quickscanr;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class RealtimeData {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventAttendeeCountListener listener;

    public interface EventAttendeeCountListener {
        void onCountUpdated(int newCount);
    }

    public void setEventListener(EventAttendeeCountListener listener) {
        this.listener = listener;
    }

    public void startListening(String eventId) {
        CollectionReference attendeesRef = db.collection("events").document(eventId).collection("attendees");

        attendeesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("RealtimeData", "Listen failed.", e);
                    return;
                }

                if (snapshots != null) {
                    int count = snapshots.size();
                    if (listener != null) {
                        listener.onCountUpdated(count);
                    }
                }
            }
        });
    }
}
