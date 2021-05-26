package com.example.mobile_signalh3.ui.fragments;

import android.Manifest;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobile_signalh3.R;
import com.example.mobile_signalh3.data.SignalListener;
import com.example.mobile_signalh3.pojo.CellLocationData;
import com.example.mobile_signalh3.pojo.SignalData;
import com.example.mobile_signalh3.ui.AppViewModel;

import de.nitri.gauge.Gauge;

public class StatisticsFragments extends BaseFragment {
    private TelephonyManager mTelephonyManager;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private SignalListener mSignalListener;
    private AppViewModel mAppViewModel;

    private Gauge gauge;
    private TextView signalTV;

    public static String simOperatorName;
    public static Location MY_LOCATION;
    public static Location TARGET_LOCATION;

    private int cellid = 0, mnc = 0, mcc = 0, lac = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gauge = (Gauge) view.findViewById(R.id.gauge);
        gauge.setLowerText("( Dbm )");
        gauge.setUpperText("Strength");

        signalTV = (TextView) view.findViewById(R.id.signal_text);

        mAppViewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);

        mTelephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        simOperatorName = mTelephonyManager.getSimOperatorName();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        GsmCellLocation gsmCellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
        if (gsmCellLocation != null) {
            String networkOperator = mTelephonyManager.getNetworkOperator();
            cellid = gsmCellLocation.getCid();
            lac = gsmCellLocation.getLac();
            mnc = Integer.parseInt(networkOperator.substring(3));
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
        }

        mAppViewModel.getCellLocationByRetrofit(cellid, mnc, mcc, lac);
        mAppViewModel.mCellLocationDataByRetrofitMutableLiveData.observe(this, new Observer<CellLocationData>() {
            @Override
            public void onChanged(CellLocationData cellLocationData) {
                TARGET_LOCATION = new Location("target");
                TARGET_LOCATION.setLongitude(cellLocationData.getLat());
                TARGET_LOCATION.setLongitude(cellLocationData.getLon());

                mAppViewModel.uploadCellLocations(cellid, cellLocationData);
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
                Log.e(StatisticsFragments.class.getSimpleName(), "Hello World! : " + location);
            }

            @Override
            public void onDataConnectionStateChanged(int state) {
                super.onDataConnectionStateChanged(state);

                Log.e(SignalStrength.class.getSimpleName(), "Hi : " + state);
            }
        };

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                MY_LOCATION = location;
                if (location != null && !mSignalListener.getResultedSignal().equalsIgnoreCase("unknown")) {
                    mAppViewModel.uploadSignals(new SignalData(location.getLatitude(), location.getLongitude(), mSignalListener.getResultedSignal()));
                }
            }
        };

        mTelephonyManager.listen(mSignalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTelephonyManager.listen(mSignalListener, PhoneStateListener.LISTEN_NONE);
        mLocationManager.removeUpdates(mLocationListener);
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