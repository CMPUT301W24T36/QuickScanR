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
/**
 * This class manages real-time data updates for event attendee counts and event counts.
 * It listens for changes in Firestore collections and notifies listeners accordingly.
 */
public class RealtimeData {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Map<String, Integer> eventToAttendeeCountMap = new HashMap<>();
    private EventAttendeeCountListener listener;
    private EventCountListener eventCountListener;

    /**
     * Callback interface to notify when total attendee count is updated.
     */
    public interface EventAttendeeCountListener {
        void onTotalCountUpdated(int totalAttendeeCount);
    }

    /**
     * Callback interface to notify when event count is updated.
     */
    public interface EventCountListener {
        void onEventCountUpdated(int eventCount);
    }

    /**
     * Sets the listener for event count updates.
     * @param listener The listener for event count updates
     */
    public void setEventCountListener(EventCountListener listener) {
        this.eventCountListener = listener;
    }

    /**
     * Sets the listener for total attendee count updates.
     * @param listener The listener for total attendee count updates
     */
    public void setEventListener(EventAttendeeCountListener listener) {
        this.listener = listener;
    }

    /**
     * Starts listening for changes in attendee counts for the specified events.
     * @param eventIds The list of event IDs to listen for
     */
    public void startListening(List<String> eventIds) {
        // Method to start listening for changes in attendee counts
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

    /**
     * Starts listening for changes in the event count for events owned by the specified user.
     * @param userId The ID of the user whose events are being monitored
     */
    public void startListeningForEventCount(String userId) {
        // Method to start listening for changes in the event count
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

    /**
     * Notifies listeners when the total attendee count is updated.
     */
    private void notifyTotalCountUpdated() {
        // Method to notify listeners when total attendee count is updated
        int totalAttendeeCount = eventToAttendeeCountMap.values().stream().mapToInt(Integer::intValue).sum();
        if (listener != null) {
            listener.onTotalCountUpdated(totalAttendeeCount);
        }
    }
}

