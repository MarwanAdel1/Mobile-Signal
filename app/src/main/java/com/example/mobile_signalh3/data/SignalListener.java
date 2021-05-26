package com.example.mobile_signalh3.data;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

public class SignalListener extends PhoneStateListener {
    private String resultedSignal;
    private int signalLevel;

    public int getSignalLevel() {
        return signalLevel;
    }

    public String getResultedSignal() {
        return resultedSignal;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);

        if (signalStrength.getCellSignalStrengths().size() >0){
            signalLevel = signalStrength.getCellSignalStrengths().get(0).getDbm();
            if (signalLevel < -120 || signalLevel > -50) {
                resultedSignal = "Dead";
            } else if (signalLevel >= -120 && signalLevel <= -110) {
                resultedSignal = "Very Poor";
            } else if (signalLevel > -110 && signalLevel <= -100) {
                resultedSignal = "Poor";
            } else if (signalLevel > -100 && signalLevel <= -90) {
                resultedSignal = "Average";
            } else if (signalLevel > -90 && signalLevel <= -80) {
                resultedSignal = "Good";
            } else if (signalLevel > -80 && signalLevel <= -50) {
                resultedSignal = "Great";
            }
        }else {
            resultedSignal = "Unknown";
        }
    }
}