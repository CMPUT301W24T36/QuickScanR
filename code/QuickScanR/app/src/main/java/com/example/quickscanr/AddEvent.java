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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEvent extends InnerPageFragment {

    private FirebaseFirestore db;

    public AddEvent() {}

    public static AddEvent newInstance(String param1, String param2) {
        AddEvent fragment = new AddEvent();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_event, container, false);
        addButtonListeners(getActivity(), v, new OrganizerEventList());

        TextInputEditText start = v.findViewById(R.id.evadd_txt_start);
        TextInputEditText end = v.findViewById(R.id.evadd_txt_end);

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

        Button addEventBtn = v.findViewById(R.id.evadd_btn_add);
        TextInputEditText name = v.findViewById(R.id.evadd_txt_name);
        TextInputEditText description = v.findViewById(R.id.evadd_txt_desc);
        TextInputEditText location = v.findViewById(R.id.evadd_txt_loc);
        TextInputEditText restrictions = v.findViewById(R.id.evadd_txt_restrictions);
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = name.getText().toString();
                String eventDescription = description.getText().toString();
                String eventLoc = location.getText().toString();
                String startDateString = start.getText().toString();
                String endDateString = end.getText().toString();
                String eventRestric = restrictions.getText().toString();

                Map<String, Object> data = new HashMap<>();
                data.put(DatabaseConstants.evNameKey, eventName);
                data.put(DatabaseConstants.evDescKey, eventDescription);
                data.put(DatabaseConstants.evLocKey, eventLoc);
                data.put(DatabaseConstants.evStartKey, startDateString);
                data.put(DatabaseConstants.evEndKey, endDateString);
                data.put(DatabaseConstants.evRestricKey, eventRestric);

                Event newEvent = new Event(eventName, eventDescription, eventLoc, startDateString, endDateString, eventRestric, MainActivity.user);

                if (!newEvent.isErrors(name, location, start, end)) {
                    db.collection(DatabaseConstants.eventColName).add(data);
                    getParentFragmentManager().beginTransaction().replace(R.id.content_main, EventDashboard.newInstance(newEvent))
                            .addToBackStack(null).commit();
                }
            }
        });
        return v;
    }

    private void onClickFuncForDates(EditText dateField) {
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                String strMonth = String.valueOf(monthOfYear + 1);
                String strDay = String.valueOf(dayOfMonth);
                String formattedMonth = strMonth.length() == 2? strMonth : "0" + strMonth;
                String formattedDay = strDay.length() == 2? strDay : "0" + strDay;
                dateField.setText(formattedDay + "-" + formattedMonth + "-" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }
}