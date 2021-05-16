package com.example.mobile_signalh3.pojo;

public class SignalData {
    private double latitude;
    private double longitude;
    private String operator;
    private String signalLevel;

    public SignalData(double latitude, double longitude, String operator, String  signalLevel) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.operator = operator;
        this.signalLevel = signalLevel;
    }

    public String getSignalLevel() {
        return signalLevel;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getOperator() {
        return operator;
    }

}
