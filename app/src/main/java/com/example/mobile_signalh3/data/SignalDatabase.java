package com.example.mobile_signalh3.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mobile_signalh3.pojo.SignalData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignalDatabase {
    private static final String BASE_URL = "https://mobile-signal-h3-default-rtdb.firebaseio.com/";
    private static SignalDatabase INSTANCE;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mgetDatabaseReference, msetDatabaseReference;

    public SignalDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mgetDatabaseReference = firebaseDatabase.getReference("First");
    }

    public static SignalDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SignalDatabase();
        }
        return INSTANCE;
    }

    public void uploadSignalToDatabase(SignalData signalData) {
        msetDatabaseReference = firebaseDatabase.getReference("First");
        msetDatabaseReference.push().setValue(signalData);
    }


    public void getSignalFromFirebaseDatabase(FirebaseCallBacks firebaseCallBacks) {
        List<SignalData> allData = new ArrayList<>();
        mgetDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        double lat = child.child("latitude").getValue(Double.class);
                        double lng = child.child("longitude").getValue(Double.class);
                        String opr = child.child("operator").getValue().toString();
                        String sig = child.child("signalLevel").getValue(String.class);

                        SignalData signalData = new SignalData(lat, lng, opr,sig);
                        allData.add(signalData);
                    }

                    firebaseCallBacks.onCallback(allData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
