package com.example.roobab.locationfetcher;

import android.net.wifi.ScanResult;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface SignalServer {

    @GET("/api/fetchCurrentLocation")
    public void fetchCurrentLocation(@Query("currentSignal") List<ScanResult> signalJson, Callback<String> cb);

}
