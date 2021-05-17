package com.example.mobile_signalh3.data;

import android.util.Log;

import com.example.mobile_signalh3.pojo.CellLocationData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientSide {
    private static final String BASE_URL = "https://api.mylnikov.org/";
    private CellLocationInterface mCellLocationInterface;
    private static ClientSide INSTANCE;

    public ClientSide() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mCellLocationInterface = retrofit.create(CellLocationInterface.class);
    }

    public static ClientSide getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientSide();
        }
        return INSTANCE;
    }

    public Call<CellLocationData> getCellLocation(/*int mcc, int mnc, int lac, int cellid*/) {
        Call<CellLocationData> call = mCellLocationInterface.getCellLocation(/*mcc,mnc,lac,cellid*/);
        return call;
    }
}
