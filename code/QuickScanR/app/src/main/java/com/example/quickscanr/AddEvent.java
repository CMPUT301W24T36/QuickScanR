/**
 * the page that allows organizers to add an event
 */

package com.example.quickscanr;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.chip.Chip;
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
    private Uri tempURI;
    private boolean registeredActivity = false;
    private ActivityResultLauncher<PickVisualMediaRequest> imgPicker;
    private Chip pickButton;

    /**
     * Constructor of AddEvent fragment
     */
    public AddEvent() {}

    /**
     * Creates a new instance of the AddEvent fragment
     * @return the fragment that is AddEvent
     */

    public static AddEvent newInstance() {
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
        addButtonListeners(getActivity(), v, new OrganizerEventList());

        // Initialize UI components
        TextInputEditText start = v.findViewById(R.id.evadd_txt_start);
        TextInputEditText end = v.findViewById(R.id.evadd_txt_end);
        TextInputEditText name = v.findViewById(R.id.evadd_txt_name);
        TextInputEditText description = v.findViewById(R.id.evadd_txt_desc);
        TextView attendeeLimText = v.findViewById(R.id.evadd_atd_lim_text);
        Switch attendeeLimit = v.findViewById(R.id.evadd_atd_lim);
        EditText attendeeLimitNumber = v.findViewById(R.id.evadd_atd_lim_num);
        AutoCompleteTextView location = v.findViewById(R.id.evadd_txt_loc); // Ensure this ID is correct in your layout
        pickButton = v.findViewById(R.id.evadd_chip_upload);

        attendeeLimitNumber.setVisibility(View.INVISIBLE);  // default no limit

        attendeeLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleAttendeeLimit(attendeeLimText, attendeeLimitNumber, isChecked);
            }
        });

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
        setupPosterAttach(v);

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

                // Prepare data for Firestore
                Map<String, Object> data = new HashMap<>();
                data.put(DatabaseConstants.evNameKey, eventName);
                data.put(DatabaseConstants.evDescKey, eventDescription);
                data.put(DatabaseConstants.evLocIdKey, selectedPlaceId);
                data.put(DatabaseConstants.evLocNameKey, eventLoc);
                data.put(DatabaseConstants.evStartKey, startDateString);
                data.put(DatabaseConstants.evEndKey, endDateString);
                long timestamp = System.currentTimeMillis();    // Get current time for timestamp
                data.put(DatabaseConstants.evTimestampKey, timestamp);
                data.put(DatabaseConstants.evSignedUpUsersKey, new ArrayList<String>());
                MainActivity mainActivity = (MainActivity) getActivity();
                String userId = mainActivity.user.getUserId();
                String userName = mainActivity.user.getName();
                data.put(DatabaseConstants.evOwnerKey, userId);
                data.put(DatabaseConstants.evAttendeeLimitKey, attendeeLimit);

                // create new event (set no limit on attendees for now)
                Event newEvent = new Event(eventName, eventDescription, eventLoc, selectedPlaceId, startDateString, endDateString, MainActivity.user, -1);

                // upload poster if exists
                if (tempURI != null) {
                    ImgHandler img = new ImgHandler(getContext());
                    Log.d("DEBUG", userId + " " + "userid, add event ");
                    img.uploadImage(tempURI, documentID -> {
                        data.put(DatabaseConstants.evPosterKey, documentID);
                    }, userId, userName);
                    newEvent.setPoster(img.uriToBitmap(tempURI));
                } else {
                    ImgHandler img = new ImgHandler(getContext());
                    img.getImage("default", bitmap -> {
                        newEvent.setPoster(bitmap);
                    });
                    data.put(DatabaseConstants.evPosterKey, "default");
                }

                // validate inputs
                if (!newEvent.isErrors(name, location, start, end, attendeeLimit, attendeeLimitNumber)) {
                    // set max attendees (either positive int for limit or -1 meaning no limit)
                    Integer attendeeLimitValue = attendeeLimit.isChecked()? Integer.parseInt(attendeeLimitNumber.getText().toString()) : -1;
                    newEvent.setMaxAttendees(attendeeLimitValue);
                    data.put(DatabaseConstants.evAttendeeLimitKey, attendeeLimitValue);

                    // Add event to Firestore and handle success
                    db.collection(DatabaseConstants.eventColName).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // On successful database entry, update event with ID and navigate to EventDashboard
                            String eventId = documentReference.getId();
                            Log.d("DEBUG", "Event " + eventId + " added successfully");
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

    /**
     * the on check changed listener for the attendee limit switch
     * @param userText tells the user if they have a limit set or not
     * @param limVal limit value field
     * @param isChecked if the switch is on or not
     */
    private void toggleAttendeeLimit(TextView userText, EditText limVal, Boolean isChecked) {
        limVal.setText("");
        if (isChecked) {
            userText.setText("Limit to:");
            limVal.setVisibility(View.VISIBLE);
        } else {
            userText.setText("No Limit");
            limVal.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * initializes the date fields (start and end) with on click listeners
     * @param dateFields list of date fields in the form (start and end dates)
     */
    private void initDatePickers(TextInputEditText... dateFields) {
        for (TextInputEditText dateField : dateFields) {
            dateField.setOnClickListener(v -> onClickFuncForDates(dateField));
        }
    }

    /**
     * Sets up image picker when attach poster chip is pressed.
     * @param view view from fragment
     */
    private void setupPosterAttach(View view) {
        //setup upload button
        if (!registeredActivity) {
            imgPicker = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    tempURI = uri;
                    ImageView imgView = view.findViewById(R.id.previewPic);
                    ImgHandler imgHandler = new ImgHandler(getContext());
                    imgView.setImageBitmap(imgHandler.uriToBitmap(uri));
                    pickButton.setText("Remove Poster");
                    removePoster(view);
                }
            });
            registeredActivity = true;
        }

        pickButton.setOnClickListener(v -> imgPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(new ActivityResultContracts.PickVisualMedia.SingleMimeType("image/*"))
                .build()));
    }

    /**
     * Called when remove poster chip is pressed, updates preview.
     * @param view view from fragment
     */
    private void removePoster(View view) {
        Chip pickButton = view.findViewById(R.id.evadd_chip_upload);
        pickButton.setOnClickListener(v -> {
            tempURI = null;
            ImageView imgView = view.findViewById(R.id.previewPic);
            imgView.setImageResource(R.drawable.close_btn_x);
            setupPosterAttach(view);
            pickButton.setText("Attach Poster File");
        });
    }

    /**
     * acts as the on click function that's called when a date is selected
     * @param dateField the field for selecting a date
     */
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