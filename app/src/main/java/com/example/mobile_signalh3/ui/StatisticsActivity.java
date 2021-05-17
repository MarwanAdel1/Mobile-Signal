package com.example.mobile_signalh3.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.data.SignalListener;
import com.example.mobile_signalh3.pojo.CellLocationData;
import com.example.mobile_signalh3.pojo.SignalData;

import de.nitri.gauge.Gauge;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    private TelephonyManager mTelephonyManager;
    private SignalListener mSignalListener;
    private AppViewModel mAppViewModel;

    private Gauge gauge;
    private TextView signalTV;
    private Button map, compass;

    public static Location MY_LOCATION;
    private int cellid = 0, mnc = 0, mcc = 0, lac = 0;

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
        compass = (Button) findViewById(R.id.compassbt);
        compass.setOnClickListener(this);

        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GsmCellLocation gsmCellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
        if (gsmCellLocation != null) {
            String networkOperator = mTelephonyManager.getNetworkOperator();
            cellid = gsmCellLocation.getCid();
            lac = gsmCellLocation.getLac();
            mnc = Integer.parseInt(networkOperator.substring(3));
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            Log.e(StatisticsActivity.class.getSimpleName(), "Hi 1 ");
        }

        mAppViewModel.getCellLocation(/*cellid, mnc, mcc, lac*/);
        mAppViewModel.mCellLocationDataMutableLiveData.observe(this, new Observer<CellLocationData>() {
            @Override
            public void onChanged(CellLocationData cellLocationData) {
                Log.e(StatisticsActivity.class.getSimpleName(), "\nCellid : " + cellid + "\nLac : " + lac
                        + "\nMnc : " + mnc + "\nMcc : " + mcc
                        + "\nCell Location : " + cellLocationData.getLat() + " - " + cellLocationData.getLon());
            }
        });

        mSignalListener = new SignalListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                displaySignal();
            }

            @Override
            public void onCellLocationChanged(CellLocation location) {
                super.onCellLocationChanged(location);
                Log.e(StatisticsActivity.class.getSimpleName(), "Hello World! : " + location);
            }
        };


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                MY_LOCATION = location;
                if (location != null && !mSignalListener.getResultedSignal().equalsIgnoreCase("unknown")) {
                    mAppViewModel.uploadSignals(new SignalData(location.getLatitude(), location.getLongitude(), mTelephonyManager.getSimOperatorName(), mSignalListener.getResultedSignal()));
                }
            }
        };

        mTelephonyManager.listen(mSignalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.map:
                Intent intentMap = new Intent(StatisticsActivity.this, MapsActivity.class);
                startActivity(intentMap);
                break;
            case R.id.compassbt:
                Intent intentcompass = new Intent(StatisticsActivity.this, CompassActivity.class);
                startActivity(intentcompass);
                break;
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