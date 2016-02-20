package com.example.roobab.locationfetcher;

import android.net.wifi.ScanResult;

import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface SignalServer {

    @Headers({"Content-Type: application/json"})
    @POST("/api/fetchCurrentLocation")
    public void fetchCurrentLocation(@Body ArrayList<List<ScanResult>> signalJson, Callback<String> cb);

}
