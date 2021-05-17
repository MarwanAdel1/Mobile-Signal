package com.example.mobile_signalh3.ui;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.data.FirebaseCallBacks;
import com.example.mobile_signalh3.pojo.SignalData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    AppViewModel mAppViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkPermission();
        mMap.setMyLocationEnabled(true);

        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        mAppViewModel.getSignals(new FirebaseCallBacks() {
            @Override
            public void onCallback(List<SignalData> signalList) {
                Drawing drawing = new Drawing();
                int res = 10;
                drawing.draw(signalList, res);
                showPolygons(drawing.getMdrawList(), drawing.getMsignalLevel());
            }
        });

        mAppViewModel.mSignalMutableLiveData.observe(this, new Observer<List<SignalData>>() {
            @Override
            public void onChanged(List<SignalData> signalList) {
                Drawing drawing = new Drawing();
                int res = 10;
                drawing.draw(signalList, res);
                showPolygons(drawing.getMdrawList(), drawing.getMsignalLevel());
            }
        });
    }

    public void showPolygons(List<List<LatLng>> drawList, List<String> signalLevel) {
        for (int n = 0; n < drawList.size(); n++) {
            switch (signalLevel.get(n)) {
                case "Dead":
                    mMap.addPolygon(new PolygonOptions().addAll(drawList.get(n)).strokeColor(Color.argb(0, 255, 87, 34)).fillColor(Color.argb(90, 255, 87, 34)));
                    break;
                case "Very Poor":
                    mMap.addPolygon(new PolygonOptions().addAll(drawList.get(n)).strokeColor(Color.argb(0, 255, 152, 0)).fillColor(Color.argb(90, 255, 152, 0)));
                    break;
                case "Poor":
                    mMap.addPolygon(new PolygonOptions().addAll(drawList.get(n)).strokeColor(Color.argb(0, 255, 193, 7)).fillColor(Color.argb(90, 255, 193, 7)));
                    break;
                case "Average":
                    mMap.addPolygon(new PolygonOptions().addAll(drawList.get(n)).strokeColor(Color.argb(0, 205, 220, 57)).fillColor(Color.argb(90, 205, 220, 57)));
                    break;
                case "Good":
                    mMap.addPolygon(new PolygonOptions().addAll(drawList.get(n)).strokeColor(Color.argb(0, 39, 195, 74)).fillColor(Color.argb(90, 39, 195, 74)));
                    break;
                case "Great":
                    mMap.addPolygon(new PolygonOptions().addAll(drawList.get(n)).strokeColor(Color.argb(0, 76, 175, 80)).fillColor(Color.argb(90, 76, 175, 80)));
                    break;
            }
        }
    }

    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }
}