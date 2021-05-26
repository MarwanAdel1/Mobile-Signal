package com.example.mobile_signalh3.data;

import com.example.mobile_signalh3.pojo.CellLocationData;
import com.example.mobile_signalh3.pojo.SignalData;

import java.util.List;

public interface FirebaseSignalsCallbacks {
    void onCallback(List<SignalData> signalList);
}