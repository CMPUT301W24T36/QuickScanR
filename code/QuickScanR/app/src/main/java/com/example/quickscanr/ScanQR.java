package com.example.quickscanr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScanQR extends AttendeeFragment {

    public ScanQR() {}

    public static ScanQR newInstance(String param1, String param2) {
        ScanQR fragment = new ScanQR();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.qr_scanning,container,false);
        addNavBarListeners(getActivity(), v);
        return v;
    }
}