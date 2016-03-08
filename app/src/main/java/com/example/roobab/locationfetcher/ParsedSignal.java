package com.example.roobab.locationfetcher;

public class ParsedSignal {
    private final String BSSID;
    private final int level;

    public ParsedSignal(String BSSID, int level) {
        this.BSSID = BSSID;
        this.level = level;
    }
}
