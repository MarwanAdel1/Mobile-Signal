package com.example.mobile_signalh3.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.data.SignalListener;
import com.example.mobile_signalh3.pojo.SignalData;

import de.nitri.gauge.Gauge;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    private TelephonyManager mTelephonyManager;
    private SignalListener mSignalListener;
    private SignalViewModel mSignalViewModel;

    private Gauge gauge;
    private TextView signalTV;
    private Button map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        gauge = (Gauge) findViewById(R.id.gauge);
        gauge.setLowerText("( Dbm )");
        gauge.setUpperText("Strength");

        signalTV = (TextView) findViewById(R.id.signal_text);
        map = (Button) findViewById(R.id.map);
        map.setOnClickListener(this);

        mSignalViewModel = ViewModelProviders.of(this).get(SignalViewModel.class);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSignalListener = new SignalListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                displaySignal();
            }
        };
        mTelephonyManager.listen(mSignalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (location != null && !mSignalListener.getResultedSignal().equalsIgnoreCase("unknown")) {
                    mSignalViewModel.uploadSignals(new SignalData(location.getLatitude(), location.getLongitude(), mTelephonyManager.getSimOperatorName(), mSignalListener.getResultedSignal()));
                }
            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.map:
                Intent intent = new Intent(StatisticsActivity.this, MapsActivity.class);
                startActivity(intent);
        }
    }

    public void displaySignal() {
        if (mSignalListener.getSignalLevel() <= -50 && mSignalListener.getSignalLevel() >= -120) {
            gauge.moveToValue(mSignalListener.getSignalLevel());
            signalTV.setText(mSignalListener.getResultedSignal());
            TextColor();
        } else {
            gauge.moveToValue(-125);
            signalTV.setText(mSignalListener.getResultedSignal());
            TextColor();
        }
    }

    public void TextColor() {
        switch (mSignalListener.getResultedSignal()) {
            case "Dead":
                signalTV.setTextColor(getResources().getColor(R.color.dead));
                break;
            case "Very Poor":
                signalTV.setTextColor(getResources().getColor(R.color.very_poor));
                break;
            case "Poor":
                signalTV.setTextColor(getResources().getColor(R.color.poor));
                break;
            case "Average":
                signalTV.setTextColor(getResources().getColor(R.color.average));
                break;
            case "Good":
                signalTV.setTextColor(getResources().getColor(R.color.good));
                break;
            case "Great":
                signalTV.setTextColor(getResources().getColor(R.color.great));
                break;
        }
    }
}