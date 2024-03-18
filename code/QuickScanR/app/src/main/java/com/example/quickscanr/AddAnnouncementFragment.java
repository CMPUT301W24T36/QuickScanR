package com.example.quickscanr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.checkerframework.checker.units.qual.A;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class AddAnnouncementFragment extends DialogFragment {

    interface AddAnnounceDialogListener {
        void addAnnouncement(Announcement announcement);
    }

    private AddAnnounceDialogListener listener;
    private Announcement announcement;
    private String userName; // Add userName variable

    // Constructor with userID
    public AddAnnouncementFragment(String userName) {
        this.userName = userName;
    }

    // Static method to create an instance of the fragment with userID
    public static AddAnnouncementFragment newInstance(String userID) {
        return new AddAnnouncementFragment(userID);
    }

    // -- Methods

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
            }
        });

        // Set positive button behavior
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Handle the click behavior of the positive button
                    // Get the text from EditText fields
                    String title = editTitle.getText().toString();
                    String body = editBody.getText().toString();
                    // Get the current date
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    // Call the listener method with the retrieved data
                    listener.addAnnouncement(new Announcement(title, body, date, userName));
                }
                // Dismiss the dialog
                dismiss();
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

        Log.d("AnncFragment", "Crash here?");
        return view;
    }
}




