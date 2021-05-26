package com.example.mobile_signalh3.data;

import com.example.mobile_signalh3.pojo.CellLocationData;

import java.util.List;

public interface FirebaseCellLocationCallbacks {
    void onCellLocationCallback(List<CellLocationData> cellLocationList);
}