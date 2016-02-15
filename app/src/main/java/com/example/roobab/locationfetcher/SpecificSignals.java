package com.example.roobab.locationfetcher;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class SpecificSignals {
    private ArrayList<TypedJsonString> apsArray;

    public SpecificSignals(ArrayList<TypedJsonString> apsArray) {
        this.apsArray = apsArray;
    }
}
