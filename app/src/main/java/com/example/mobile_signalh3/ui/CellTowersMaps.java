package com.example.mobile_signalh3.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.data.FirebaseCellLocationCallbacks;
import com.example.mobile_signalh3.pojo.CellLocationData;
import com.example.mobile_signalh3.ui.fragments.StatisticsFragments;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CellTowersMaps extends FragmentActivity implements OnMapReadyCallback, SearchView.OnQueryTextListener {
    private AppViewModel mAppViewModel;
    private SupportMapFragment mapFragment;
    private View mapView;

    private GoogleMap mMap;

    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_towers_maps);

        searchView = findViewById(R.id.idSearchView2);
        searchView.setOnQueryTextListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mAppViewModel.getCellLocation(new FirebaseCellLocationCallbacks() {
            @Override
            public void onCellLocationCallback(List<CellLocationData> cellLocationList) {
                showCellLocationTowers(cellLocationList);
            }
        });

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            customizeMyLocationButton();
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
        mMap.setMinZoomPreference(14);
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

    private void showCellLocationTowers(List<CellLocationData> cellLocationList) {
        List<LatLng> markers = new ArrayList<>();
        for (int i = 0; i < cellLocationList.size(); i++) {
            LatLng latLng = new LatLng(cellLocationList.get(i).getLat(), cellLocationList.get(i).getLon());
            markers.add(latLng);
        }

        if (markers.size() > 0) {
            int id;
            String title;
            Log.e(CellTowersMaps.class.getSimpleName(), "Sim Operator : " + StatisticsFragments.simOperatorName);
            if (StatisticsFragments.simOperatorName.equalsIgnoreCase("Orange EG")) {
                id = R.drawable.orange;
                title = "Orange EG";
            } else if (StatisticsFragments.simOperatorName.equalsIgnoreCase("Etisalat")) {
                id = R.drawable.etisalat;
                title = "Etisalat EG";
            } else if (StatisticsFragments.simOperatorName.equalsIgnoreCase("Vodafone")) {
                id = R.drawable.vodafone;
                title = "Vodafone EG";
            } else if (StatisticsFragments.simOperatorName.equalsIgnoreCase("We")) {
                id = R.drawable.we;
                title = "We EG";
            } else {
                id = R.drawable.unknown;
                title = "Unknown";
            }

            for (int i = 0; i < markers.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .position(markers.get(i))
                        .title(title)
                        .icon(BitmapFromVector(getApplicationContext(), id)));
            }
        }
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String location = searchView.getQuery().toString();
        List<Address> addressList = null;
        if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(CellTowersMaps.this);
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
                Toast.makeText(CellTowersMaps.this,"Not Found",Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}