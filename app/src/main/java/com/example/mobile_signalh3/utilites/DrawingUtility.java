package com.example.mobile_signalh3.utilites;

import android.util.Log;

import com.example.mobile_signalh3.pojo.SignalData;
import com.example.mobile_signalh3.ui.fragments.SignalMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;

import java.util.ArrayList;
import java.util.List;

public class DrawingUtility {
    private List<List<LatLng>> mdrawList;
    private List<String> msignalLevel;

    public List<List<LatLng>> getMdrawList() {
        return mdrawList;
    }

    public List<String> getMsignalLevel() {
        return msignalLevel;
    }

    public DrawingUtility draw(List<SignalData> signalList, int res) {
        List<List<LatLng>> drawList = new ArrayList<>();
        List<String> signalLevel = new ArrayList<>();
        List<List<Integer>> averageSignal = new ArrayList<>();

        H3Core h3 = H3Core.newSystemInstance();

        for (int i = 0; i < signalList.size(); i++) {
            List<LatLng> tempList = new ArrayList<>();

            boolean flag = false;

            String signal = signalList.get(i).getSignalLevel();
            int signalStrConvertedToLvl = convertSignalStrToSignalLevels(signal);

            String hexAddr = h3.geoToH3Address(signalList.get(i).getLatitude(), signalList.get(i).getLongitude(), res);
            List<GeoCoord> polygonPoints = h3.h3ToGeoBoundary(hexAddr);

            for (int j = 0; j < polygonPoints.size(); j++) {
                LatLng latLng = new LatLng(polygonPoints.get(j).lat, polygonPoints.get(j).lng);
                tempList.add(latLng);
            }

            List<Integer> repeatedSignal = new ArrayList<>();
            if (drawList.size() == 0) {
                drawList.add(tempList);
                signalLevel.add(signal);
                repeatedSignal.add(signalStrConvertedToLvl);
                averageSignal.add(0, repeatedSignal);
            } else {
                for (int k = 0; k < drawList.size(); k++) {
                    if (drawList.get(k).equals(tempList)) {
                        flag = true;
                        averageSignal.get(k).add(signalStrConvertedToLvl);
                    }
                }
                if (!flag) {
                    drawList.add(tempList);
                    signalLevel.add(signal);
                    repeatedSignal.add(signalStrConvertedToLvl);
                    averageSignal.add(repeatedSignal);
                }
            }
        }
        signalLevel = getSignalAfterCalculateAverage(averageSignal);
        msignalLevel = signalLevel;
        mdrawList = drawList;

        DrawingUtility mDrawingUtility = new DrawingUtility();
        mDrawingUtility.msignalLevel = signalLevel;
        mDrawingUtility.mdrawList = drawList;

        return mDrawingUtility;

    }

    public int convertSignalStrToSignalLevels(String signal) {
        int level;
        switch (signal) {
            case "Very Poor":
                level = 1;
                break;
            case "Poor":
                level = 2;
                break;
            case "Average":
                level = 3;
                break;
            case "Good":
                level = 4;
                break;
            case "Great":
                level = 5;
                break;
            default:
                level = 0;
        }
        return level;
    }

    public String convertSignalLevelToSignalStr(int signal) {
        String level;
        switch (signal) {
            case 1:
                level = "Very Poor";
                break;
            case 2:
                level = "Poor";
                break;
            case 3:
                level = "Average";
                break;
            case 4:
                level = "Good";
                break;
            case 5:
                level = "Great";
                break;
            default:
                level = "Dead";
        }
        return level;
    }

    public List<String> getSignalAfterCalculateAverage(List<List<Integer>> oldList) {
        List<String> newList = new ArrayList<>();
        for (int i = 0; i < oldList.size(); i++) {
            int average;
            int sum = 0;
            for (int j = 0; j < oldList.get(i).size(); j++) {
                sum += oldList.get(i).get(j);
            }
            average = sum / oldList.get(i).size();
            newList.add(convertSignalLevelToSignalStr(average));
        }
        Log.e(SignalMapFragment.class.getSimpleName(), "Oops Crash : \nSize  = " + newList.size() + "\n" + newList.toString());
        return newList;
    }
}
