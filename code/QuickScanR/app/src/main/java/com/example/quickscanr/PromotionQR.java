package com.example.quickscanr; // Use your own package name

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// NOT YET FUNCTIONAL
public class PromotionQR extends InnerPageFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.qr_promotional, container, false);
        addButtonListeners(getActivity(), v);
        return v;
    }
}
