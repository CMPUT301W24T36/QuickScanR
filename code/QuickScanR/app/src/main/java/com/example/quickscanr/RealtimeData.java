package com.example.quickscanr;

import android.util.Log;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealtimeData {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Map<String, Integer> eventToAttendeeCountMap = new HashMap<>();
    private EventAttendeeCountListener listener;

    public interface EventAttendeeCountListener {
        void onTotalCountUpdated(int totalAttendeeCount);
    }

    public void setEventListener(EventAttendeeCountListener listener) {
        this.listener = listener;
    }

    public void startListening(List<String> eventIds) {
        for (String eventId : eventIds) {
            db.collection("events").document(eventId).collection("attendees")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("RealtimeData", "Listen failed.", e);
                                return;
                            }

                            if (snapshots != null) {
                                int currentCount = snapshots.size();
                                eventToAttendeeCountMap.put(eventId, currentCount);
                                notifyTotalCountUpdated();
                            }
                        }
                    });
        }
    }

    private void notifyTotalCountUpdated() {
        int totalAttendeeCount = eventToAttendeeCountMap.values().stream().mapToInt(Integer::intValue).sum();
        if (listener != null) {
            listener.onTotalCountUpdated(totalAttendeeCount);
        }
    }
}

