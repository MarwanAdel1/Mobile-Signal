package com.example.mobile_signalh3.data;

import com.example.mobile_signalh3.pojo.SignalData;

import java.util.List;

public interface FirebaseCallBacks {
    void onCallback(List<SignalData> signalList);
}
