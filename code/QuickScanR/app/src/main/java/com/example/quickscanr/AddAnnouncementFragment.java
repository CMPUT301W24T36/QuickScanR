/**
 * This file is responsible for prompting to the User to make an announcement
 * References:
 * For rounded corners: https://stackoverflow.com/questions/15421271/custom-fragmentdialog-with-round-corners-and-not-100-screen-width
 * For positive button: https://stackoverflow.com/questions/15912124/android-disable-dialogfragment-ok-cancel-buttons
 */
package com.example.quickscanr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quickscanr.Announcement;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * This class is how the organizer is able to make an announcement.
 * This is where the input goes.
 * @see Announcement to see what is getting created.
 * @see OrganizerHome to see when it gets called
 */
public class AddAnnouncementFragment extends DialogFragment {

    /**
     * The interface below allows us to talk to OrganizerHome for
     * 1) Adding announcement to database
     * 2) Dismissing the focus on the editText (what triggers this DialogFragment)
     */
    interface AddAnnounceDialogListener {
        void addAnnouncement(Announcement announcement);

        void inDismiss();
    }

    // Class variables
    private AddAnnounceDialogListener listener;
    private String userName; // Add userName variable
    private String userID;
    private Spinner eventSpinner;
    private String selectedEventId;
    private String userId;

    /**
     * Constructor
     *
     * @param userName is the one that is announcing!
     */
    public AddAnnouncementFragment(String userName) {
        this.userName = userName;
        this.userID = userID;
    }

    /**
     * This one checks if the parent fragment (Organizer Home) has the listener that we need!
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Check if the parent fragment is an instance of AddAnnounceDialogListener
        if (getParentFragment() instanceof AddAnnounceDialogListener) {
            listener = (AddAnnounceDialogListener) getParentFragment();
        } else {
            assert getParentFragment() != null;
            throw new RuntimeException(getParentFragment().getClass().getSimpleName()
                    + " must implement AddAnnounceDialogListener");
        }
    }


    /**
     * We are creating the Dialog here.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return the dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d("AnncFragment", "in OnCreateDialog");
        // Use AlertDialog.Builder to create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.add_announcement_fragment, null);

        eventSpinner = view.findViewById(R.id.event_spinner);
        fetchEvents();

        // Find the EditText fields in the inflated layout
        EditText editTitle = view.findViewById(R.id.an_title_edit_text);
        EditText editBody = view.findViewById(R.id.an_body_edit_text);

        // Find the buttons in the inflated layout
        Button positiveButton = view.findViewById(R.id.annc_add_button);
        Button negativeButton = view.findViewById(R.id.annc_cancel_button);

        // Set negative button behavior
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when negative button is clicked
                dismiss();
                listener.inDismiss();// Tell the parent fragment to stop the focus!
            }
        });

        // Set positive button behavior
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View no_event = view.findViewById(R.id.no_event_handler);
                no_event.setVisibility(View.GONE);
                // Get the text from EditText fields
                String title = editTitle.getText().toString();
                String body = editBody.getText().toString();

                // Check if an event is selected
                if (selectedEventId == null || selectedEventId.isEmpty()) {
                    // Display a message to the user indicating that an event must be selected
                    no_event.setVisibility(View.VISIBLE);
                    return; // Stop further execution, keep them in the fragment.
                }

                if (listener != null) {
                    // Get the current date
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    // Call the listener method with the retrieved data
                    listener.addAnnouncement(new Announcement(title, body, date, userID, userName, selectedEventId));
                }
                // Dismiss the dialog
                dismiss();
                listener.inDismiss(); // Tell the parent fragment to stop the focus!
            }
        });



        // Set the view of the dialog and create it
        builder.setView(view);
        return builder.create();
    }

    /**
     * This makes the dialog rounded and look nicer.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_announcement_fragment, container, false);
        // Set transparent background and no title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return view;
    }

    /**
     * Fetches the events from the database
     */
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MainActivity mainActivity = (MainActivity) getActivity();
        userId = mainActivity.user.getUserId();
        List<String> eventNames = new ArrayList<>();
        Map<String, String> eventNameToId = new HashMap<>();

        db.collection("events")
                .whereEqualTo("ownerID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventName = document.getString("name");
                            String eventId = document.getId();
                            eventNames.add(eventName);
                            eventNameToId.put(eventName, eventId);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, eventNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        eventSpinner.setAdapter(adapter);

                        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String eventName = (String) parent.getItemAtPosition(position);
                                selectedEventId = eventNameToId.get(eventName);
                                Log.d("AddAnnouncementFragment", "Selected event ID: " + selectedEventId);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    } else {
                        Log.e("FetchEvents", "Error getting documents: ", task.getException());
                    }
                });
    }
}




