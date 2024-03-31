package com.example.quickscanr;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    private String selectedPlaceId;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_event, container, false);

        // Initialize UI components
        TextInputEditText start = v.findViewById(R.id.evadd_txt_start);
        TextInputEditText end = v.findViewById(R.id.evadd_txt_end);
        TextInputEditText name = v.findViewById(R.id.evadd_txt_name);
        TextInputEditText description = v.findViewById(R.id.evadd_txt_desc);
        TextInputEditText restrictions = v.findViewById(R.id.evadd_txt_restrictions);
        AutoCompleteTextView location = v.findViewById(R.id.evadd_txt_loc); // Ensure this ID is correct in your layout

        PlaceAutoSuggestAdapter adapter = new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1);
        location.setAdapter(adapter);
        location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place selectedPlace = adapter.getItemInfo(position);
                if (selectedPlace != null) {
                    selectedPlaceId = selectedPlace.placeId;
                    location.setText(selectedPlace.name, false);
                }
            }
        });

        Button addEventBtn = v.findViewById(R.id.evadd_btn_add);
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect input from the user
                String eventName = name.getText().toString();
                String eventDescription = description.getText().toString();
                String eventLoc = location.getText().toString();
                String startDateString = start.getText().toString();
                String endDateString = end.getText().toString();
                String eventRestrictions = restrictions.getText().toString();

                // Prepare data for Firestore
                Map<String, Object> data = new HashMap<>();
                data.put(DatabaseConstants.evNameKey, eventName);
                data.put(DatabaseConstants.evDescKey, eventDescription);
                data.put(DatabaseConstants.evLocIdKey, selectedPlaceId);
                data.put(DatabaseConstants.evLocNameKey, eventLoc);
                data.put(DatabaseConstants.evStartKey, startDateString);
                data.put(DatabaseConstants.evEndKey, endDateString);
                data.put(DatabaseConstants.evRestricKey, eventRestrictions);
                long timestamp = System.currentTimeMillis();    // Get current time for timestamp
                data.put(DatabaseConstants.evTimestampKey, timestamp);
                data.put(DatabaseConstants.evPosterKey, "default");
                data.put(DatabaseConstants.evSignedUpUsersKey, new ArrayList<String>());
                MainActivity mainActivity = (MainActivity) getActivity();
                String userId = mainActivity.user.getUserId();
                data.put(DatabaseConstants.evOwnerKey, userId);

                // Create Event object and validate inputs
                Event newEvent = new Event(eventName, eventDescription, eventLoc, selectedPlaceId, startDateString, endDateString, eventRestrictions, MainActivity.user);
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

        // Initialize date pickers for start and end date fields
        initDatePickers(start, end);

        return v;
    }

    private void initDatePickers(TextInputEditText... dateFields) {
        for (TextInputEditText dateField : dateFields) {
            dateField.setOnClickListener(v -> onClickFuncForDates(dateField));
        }
    }

    private void onClickFuncForDates(EditText dateField) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            String formattedMonth = (monthOfYear + 1 < 10) ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
            String formattedDay = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
            dateField.setText(formattedDay + "-" + formattedMonth + "-" + year1);
        }, year, month, day);

        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }
}