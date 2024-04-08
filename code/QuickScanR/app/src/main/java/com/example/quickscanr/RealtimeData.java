package com.example.quickscanr;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.Nullable;

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
    private EventCountListener eventCountListener;

    public interface EventAttendeeCountListener {
        void onTotalCountUpdated(int totalAttendeeCount);
    }

    public interface EventCountListener {
        void onEventCountUpdated(int eventCount);
    }

    public void setEventCountListener(EventCountListener listener) {
        this.eventCountListener = listener;
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

    public void startListeningForEventCount(String userId) {
        db.collection("events")
                .whereEqualTo("ownerID", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("RealtimeData", "Listening for event count updates failed.", e);
                            return;
                        }

                        if (snapshots != null) {
                            int eventCount = snapshots.size();
                            if (eventCountListener != null) {
                                eventCountListener.onEventCountUpdated(eventCount);
                            }
                        }
                    }
                });
    }

    private void notifyTotalCountUpdated() {
        int totalAttendeeCount = eventToAttendeeCountMap.values().stream().mapToInt(Integer::intValue).sum();
        if (listener != null) {
            listener.onTotalCountUpdated(totalAttendeeCount);
        }
    }
}

