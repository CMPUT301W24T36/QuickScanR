package com.example.quickscanr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    Integer userType = UserType.ATTENDEE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new AttendeeHome())
                .addToBackStack(null).commit();
    }

}