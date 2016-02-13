package com.example.roobab.locationfetcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.net.wifi.WifiManager.RSSI_CHANGED_ACTION;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;

public class MainActivity extends AppCompatActivity {
    private TextView findLocationButton;
    private TextView currentLocationView;
    private TextView locationNameView;
    private RadioGroup choices;
    private static final int MSG_FETCH_WIFI_STRENGTH = 1;

    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FETCH_WIFI_STRENGTH:
                    startScan();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupWifiSignalReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceivers();
    }

    private void startScan() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.startScan();
    }

    private void setUpViews() {
        findLocationButton = (TextView) findViewById(R.id.button);
        currentLocationView = (TextView) findViewById(R.id.textView);
        locationNameView = (TextView) findViewById(R.id.textView2);
        choices = (RadioGroup) findViewById(R.id.choices);

        findLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readSignalStrength();
        }
    };

    private void readSignalStrength() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        List<ScanResult> aps = wifiManager.getScanResults();
    }

    private void unregisterReceivers() {
        unregisterReceiver(wifiReceiver);
    }

    private void getCurrentLocation() {
        recordCurrentWifiSignals();
    }

    private void recordCurrentWifiSignals() {

    }

    private void setupWifiSignalReceivers() {
        registerReceiver(wifiReceiver, new IntentFilter(CONNECTIVITY_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(RSSI_CHANGED_ACTION));
    }
}
