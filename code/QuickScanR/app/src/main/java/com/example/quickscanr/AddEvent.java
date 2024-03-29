package com.example.quickscanr;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Fragment class for adding events
 */
public class AddEvent extends InnerPageFragment {

    private FirebaseFirestore db;

    /**
     * Constructor of AddEvent fragment
     */
    public AddEvent() {}

    /**
     * Creates a new instance of the AddEvent fragment
     * @param param1
     * @param param2
     * @return the fragment that is AddEvent
     */

    public static AddEvent newInstance(String param1, String param2) {
        AddEvent fragment = new AddEvent();
        return fragment;
    }


    /**
     * Initializes the db variable
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates the view relevant for AddEvent
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the View created
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_event, container, false); // Inflate layout for this fragment
        addButtonListeners(getActivity(), v, new OrganizerEventList()); // Set up button listeners (method not shown here)

        // Initialize UI components
        TextInputEditText start = v.findViewById(R.id.evadd_txt_start);
        TextInputEditText end = v.findViewById(R.id.evadd_txt_end);

        // Set click listeners for date input fields
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFuncForDates(start);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFuncForDates(end);
            }
        });

        // Set up the Add Event button and input fields
        Button addEventBtn = v.findViewById(R.id.evadd_btn_add);
        TextInputEditText name = v.findViewById(R.id.evadd_txt_name);
        TextInputEditText description = v.findViewById(R.id.evadd_txt_desc);
        TextInputEditText location = v.findViewById(R.id.evadd_txt_loc);
        TextInputEditText restrictions = v.findViewById(R.id.evadd_txt_restrictions);

        // Handle Add Event button click
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect input from the user
                long timestamp = System.currentTimeMillis(); // Get current time for timestamp
                String eventName = name.getText().toString();
                String eventDescription = description.getText().toString();
                String eventLoc = location.getText().toString();
                String startDateString = start.getText().toString();
                String endDateString = end.getText().toString();
                String eventRestric = restrictions.getText().toString();

                // Prepare data for Firestore
                Map<String, Object> data = new HashMap<>();
                data.put(DatabaseConstants.evNameKey, eventName);
                data.put(DatabaseConstants.evDescKey, eventDescription);
                data.put(DatabaseConstants.evLocKey, eventLoc);
                data.put(DatabaseConstants.evStartKey, startDateString);
                data.put(DatabaseConstants.evEndKey, endDateString);
                data.put(DatabaseConstants.evRestricKey, eventRestric);
                data.put(DatabaseConstants.evTimestampKey, timestamp);
                data.put(DatabaseConstants.evPosterKey, "");  // TO BE REPLACED
                data.put("signedUpUsers", new ArrayList<String>());
                MainActivity mainActivity = (MainActivity) getActivity();
                String userId = mainActivity.user.getUserId();
                data.put(DatabaseConstants.evOwnerKey, userId);

                // Create Event object and validate inputs
                Event newEvent = new Event(eventName, eventDescription, eventLoc, startDateString, endDateString, eventRestric, MainActivity.user);
                if (!newEvent.isErrors(name, location, start, end)) {
                    // Add event to Firestore and handle success
                    db.collection(DatabaseConstants.eventColName).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // On successful database entry, update event with ID and navigate to EventDashboard
                            String eventId = documentReference.getId();
                            newEvent.setId(eventId);
                            newEvent.setTimestamp(timestamp); // Update event timestamp
                            newEvent.setSignedUp(new ArrayList<>());
                            // Navigate to the EventDashboard fragment showing the new event
                            getParentFragmentManager().beginTransaction().replace(R.id.content_main, EventDashboard.newInstance(newEvent))
                                    .addToBackStack(null).commit();
                        }
                    });
                }
            }
        });
        return v;
    }


    /**
     * Handles clicks for date input fields by displaying a DatePicker dialog
     * @param dateField
     */

    private void onClickFuncForDates(EditText dateField) {
        final Calendar c = Calendar.getInstance();

        // Get current date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create and show date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Format the date and set it in the EditText
                String strMonth = String.valueOf(monthOfYear + 1);
                String strDay = String.valueOf(dayOfMonth);
                String formattedMonth = strMonth.length() == 2? strMonth : "0" + strMonth;
                String formattedDay = strDay.length() == 2? strDay : "0" + strDay;
                dateField.setText(formattedDay + "-" + formattedMonth + "-" + year); // Set formatted date
            }
        }, year, month, day);
        datePickerDialog.show(); // Show the dialog to pick a date
        // Set button text colors
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }
}