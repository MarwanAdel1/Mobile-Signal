package com.example.mobile_signalh3.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
    }
}
