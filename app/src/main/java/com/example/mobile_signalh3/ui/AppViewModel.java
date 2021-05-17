package com.example.mobile_signalh3.ui;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile_signalh3.data.ClientSide;
import com.example.mobile_signalh3.data.FirebaseCallBacks;
import com.example.mobile_signalh3.data.SignalDatabase;
import com.example.mobile_signalh3.pojo.CellLocationData;
import com.example.mobile_signalh3.pojo.SignalData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppViewModel extends ViewModel {
    MutableLiveData<List<SignalData>> mSignalMutableLiveData= new MutableLiveData<>();
    MutableLiveData<CellLocationData> mCellLocationDataMutableLiveData = new MutableLiveData<>();

    public  void getSignals(FirebaseCallBacks firebaseCallBacks){
         SignalDatabase.getInstance().getSignalFromFirebaseDatabase(firebaseCallBacks);
         //mSignalMutableLiveData.setValue();
    }

    public void uploadSignals(SignalData signalData){
        SignalDatabase.getInstance().uploadSignalToDatabase(signalData);
    }

    public void getCellLocation(/*int cellid,int mnc,int mcc,int lac*/){
        ClientSide.getInstance().getCellLocation(/*mcc, mnc, lac, cellid*/).enqueue(new Callback<CellLocationData>() {
            @Override
            public void onResponse(Call<CellLocationData> call, Response<CellLocationData> response) {
                Log.e(AppViewModel.class.getSimpleName(),"Marwan's World : "+response.toString());
                mCellLocationDataMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<CellLocationData> call, Throwable t) {
                Log.e(AppViewModel.class.getSimpleName(),"Marwan's World : wrong :"+t.getMessage());
            }
        });
    }

}
