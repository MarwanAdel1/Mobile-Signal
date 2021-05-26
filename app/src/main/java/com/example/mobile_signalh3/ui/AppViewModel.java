package com.example.mobile_signalh3.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile_signalh3.data.ClientSide;
import com.example.mobile_signalh3.data.FirebaseCellLocationCallbacks;
import com.example.mobile_signalh3.data.FirebaseSignalsCallbacks;
import com.example.mobile_signalh3.data.MyFirebaseDatabase;
import com.example.mobile_signalh3.pojo.CellLocationData;
import com.example.mobile_signalh3.pojo.CellLocationResponse;
import com.example.mobile_signalh3.pojo.SignalData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppViewModel extends ViewModel {
    public MutableLiveData<CellLocationData> mCellLocationDataByRetrofitMutableLiveData = new MutableLiveData<>();

    public void getSignals(FirebaseSignalsCallbacks firebaseSignalsCallbacks) {
        MyFirebaseDatabase.getInstance().getSignalFromFirebaseDatabase(firebaseSignalsCallbacks);
        //mSignalMutableLiveData.setValue();
    }

    public void getCellLocation(FirebaseCellLocationCallbacks firebaseCellLocationCallbacks) {
        MyFirebaseDatabase.getInstance().getCellLocationFromFirebaseDatabase(firebaseCellLocationCallbacks);
        //mCellLocationDataMutableLiveData.setValue();
    }

    public void uploadSignals(SignalData signalData) {
        MyFirebaseDatabase.getInstance().uploadSignalToDatabase(signalData);
    }

    public void uploadCellLocations(int cellid,CellLocationData cellLocationData) {
        MyFirebaseDatabase.getInstance().uploadCellLocationToDatabase(cellid,cellLocationData);
    }

    public void getCellLocationByRetrofit(int cellid, int mnc, int mcc, int lac) {
        ClientSide.getInstance().getCellLocation(mcc, mnc, lac, cellid).enqueue(new Callback<CellLocationResponse>() {
            @Override
            public void onResponse(Call<CellLocationResponse> call, Response<CellLocationResponse> response) {
                CellLocationData cellLocationData = new CellLocationData();
                cellLocationData.setLat(response.body().getData().getLat());
                cellLocationData.setLon(response.body().getData().getLon());
                cellLocationData.setRange(response.body().getData().getRange());
                cellLocationData.setTime(response.body().getData().getTime());

                mCellLocationDataByRetrofitMutableLiveData.setValue(cellLocationData);
            }

            @Override
            public void onFailure(Call<CellLocationResponse> call, Throwable t) {

            }
        });
    }
}