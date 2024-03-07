package com.example.quickscanr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class CheckedInAttendeeList extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Map<String, Object>> attendeesData = new ArrayList<>();
    private CheckedInAttendeeAdapter adapter;
    private RecyclerView recyclerView;

    // New instance method to pass event ID
    public static CheckedInAttendeeList newInstance(String eventId) {
        CheckedInAttendeeList fragment = new CheckedInAttendeeList();
        Bundle args = new Bundle();
        args.putString("EVENT_ID", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checked_users_list, container, false);

        recyclerView = view.findViewById(R.id.chkd_usrs_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new CheckedInAttendeeAdapter(attendeesData);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            String eventId = getArguments().getString("EVENT_ID");
            setupFirestoreRealtimeUpdate(eventId);
        }

        return view;
    }

    private void setupFirestoreRealtimeUpdate(String eventId) {
        final CollectionReference attendeesRef = db.collection("events").document(eventId).collection("attendees");

        attendeesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("CheckedInAttendeeList", "Listen failed.", e);
                    return;
                }

                List<Map<String, Object>> updatedAttendeesData = new ArrayList<>();
                if (snapshots != null) {
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Map<String, Object> attendeeData = new HashMap<>();
                        attendeeData.put("userId", doc.getId());
                        attendeeData.put("name", doc.getString("name"));
                        attendeeData.put("checkIns", doc.get("checkIns"));
                        updatedAttendeesData.add(attendeeData);
                    }
                }

                attendeesData.clear();
                attendeesData.addAll(updatedAttendeesData);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
