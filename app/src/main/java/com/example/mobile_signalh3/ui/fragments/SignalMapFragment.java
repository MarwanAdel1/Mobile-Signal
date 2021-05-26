package com.example.mobile_signalh3.ui.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.data.FirebaseSignalsCallbacks;
import com.example.mobile_signalh3.pojo.SignalData;
import com.example.mobile_signalh3.ui.AppViewModel;
import com.example.mobile_signalh3.ui.CellTowersMaps;
import com.example.mobile_signalh3.utilites.DrawingUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;

public class SignalMapFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private View mapView;
    private AppViewModel mAppViewModel;

    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.idSearchView1);
        searchView.setOnQueryTextListener(this);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                checkPermission();
                mMap.setMyLocationEnabled(true);

                if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                    customizeMyLocationButton();
                }
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                mMap.setMinZoomPreference(14);

                mAppViewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);

                mAppViewModel.getSignals(new FirebaseSignalsCallbacks() {
                    @Override
                    public void onCallback(List<SignalData> signalList) {
                        DrawingUtility drawingUtility = new DrawingUtility();
                        int res = 10;
                        drawingUtility.draw(signalList, res);
                        showPolygons(drawingUtility.getMdrawList(), drawingUtility.getMsignalLevel());
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
        });
    }

    private void customizeMyLocationButton(){
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        ImageView locationImage = (ImageView) locationButton;
        locationImage.setImageResource(R.drawable.mylocation_button);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 100, 50);
    }

    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getContext().getApplicationContext(), ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getContext().getApplicationContext(), READ_PHONE_STATE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getContext().getApplicationContext(), ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String location = searchView.getQuery().toString();
        List<Address> addressList = null;
        if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }else {
                Toast.makeText(getActivity(),"Not Found",Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}