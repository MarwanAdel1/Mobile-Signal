package com.example.mobile_signalh3.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile_signalh3.data.FirebaseCallBacks;
import com.example.mobile_signalh3.data.SignalDatabase;
import com.example.mobile_signalh3.pojo.SignalData;

import java.util.List;

public class SignalViewModel extends ViewModel {
    MutableLiveData<List<SignalData>> mSignalMutableLiveData= new MutableLiveData<>();

    public  void getSignals(FirebaseCallBacks firebaseCallBacks){
         SignalDatabase.getInstance().getSignalFromFirebaseDatabase(firebaseCallBacks);
         //mSignalMutableLiveData.setValue();
    }

    public void uploadSignals(SignalData signalData){
        SignalDatabase.getInstance().uploadSignalToDatabase(signalData);
    }

}
