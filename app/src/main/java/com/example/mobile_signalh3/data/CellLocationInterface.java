package com.example.mobile_signalh3.data;

import com.example.mobile_signalh3.pojo.CellLocationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CellLocationInterface {
    @GET("geolocation/cell?v=1.1&data=open")
    public Call<CellLocationResponse> getCellLocation(@Query("mcc") int mcc,
                                                  @Query("mnc") int mnc,
                                                  @Query("lac") int lac,
                                                  @Query("cellid")int cellid);
}