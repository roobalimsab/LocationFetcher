package com.example.roobab.locationfetcher;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SignalServer {

    @GET("/api/fetchCurrentLocation")
    public void fetchCurrentLocation(@Query("currentSignal") String parsedSignals, Callback<String> cb);

}
