package com.example.mobile_signalh3.data;

import androidx.annotation.NonNull;

import com.example.mobile_signalh3.pojo.CellLocationData;
import com.example.mobile_signalh3.pojo.SignalData;
import com.example.mobile_signalh3.ui.fragments.StatisticsFragments;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyFirebaseDatabase {
    private static MyFirebaseDatabase INSTANCE;
    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference mgetDatabaseReference;
    private DatabaseReference msetDatabaseReference;

    public MyFirebaseDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mgetDatabaseReference = firebaseDatabase.getReference("Database");
    }

    public static MyFirebaseDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MyFirebaseDatabase();
        }
        return INSTANCE;
    }

    public void uploadSignalToDatabase(SignalData signalData) {
        msetDatabaseReference = firebaseDatabase.getReference("Database");
        msetDatabaseReference.child("SignalData").child(StatisticsFragments.simOperatorName).push().setValue(signalData);
    }

    public void getSignalFromFirebaseDatabase(FirebaseSignalsCallbacks firebaseSignalsCallbacks) {
        List<SignalData> allData = new ArrayList<>();
        mgetDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot child : snapshot.child("SignalData").child(StatisticsFragments.simOperatorName).getChildren()) {
                        double lat = child.child("latitude").getValue(Double.class);
                        double lng = child.child("longitude").getValue(Double.class);
                        String sig = child.child("signalLevel").getValue(String.class);

                        SignalData signalData = new SignalData(lat, lng, sig);
                        allData.add(signalData);
                    }
                    firebaseSignalsCallbacks.onCallback(allData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getCellLocationFromFirebaseDatabase(FirebaseCellLocationCallbacks firebaseCellLocationCallbacks) {
        List<CellLocationData> allData = new ArrayList<>();
        mgetDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot child : snapshot.child("CellLocationData").child(StatisticsFragments.simOperatorName).getChildren()) {
                        CellLocationData cellLocationData = new CellLocationData();
                        cellLocationData.setLat(child.child("lat").getValue(Double.class));
                        cellLocationData.setLon(child.child("lon").getValue(Double.class));
                        cellLocationData.setRange(child.child("range").getValue(Double.class));
                        cellLocationData.setTime(child.child("time").getValue(Long.class));

                        allData.add(cellLocationData);
                    }
                    firebaseCellLocationCallbacks.onCellLocationCallback(allData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void uploadCellLocationToDatabase(int cellid, CellLocationData cellLocationData) {
        msetDatabaseReference = firebaseDatabase.getReference("Database");
        msetDatabaseReference.child("CellLocationData").child(StatisticsFragments.simOperatorName).child(String.valueOf(cellid)).setValue(cellLocationData);
    }
}