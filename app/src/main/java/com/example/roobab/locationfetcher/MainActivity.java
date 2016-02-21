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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.net.wifi.WifiManager.RSSI_CHANGED_ACTION;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;

public class MainActivity extends AppCompatActivity {
    private TextView findLocationButton;
    private TextView currentLocationView;
    private TextView locationNameView;
    private RadioGroup choices;
    private SignalServer signalServer;
    private List<ScanResult> aps = new ArrayList<>();
    private static final int MSG_FETCH_WIFI_STRENGTH = 123;

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

    private void startScan() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.startScan();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signalServer = getSignalServer();
        setUpViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupWifiSignalReceivers();
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^in on receive of wifi signals");
            readSignalStrength();
        }
    };

    private void setupWifiSignalReceivers() {
        registerReceiver(wifiReceiver, new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    private void unregisterReceivers() {
        unregisterReceiver(wifiReceiver);
    }

    private void setUpViews() {
        findLocationButton = (TextView) findViewById(R.id.button);
        currentLocationView = (TextView) findViewById(R.id.textView);
        locationNameView = (TextView) findViewById(R.id.textView2);
        locationNameView.setText("");
        choices = (RadioGroup) findViewById(R.id.choices);

        findLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationNameView.setText("Pending");
                getCurrentLocation();
            }
        });
    }

    private void readSignalStrength() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        aps = wifiManager.getScanResults();
        List<ScanResult> filteredAps = new ArrayList<>();
        for(ScanResult ap : aps) {
//            if(ap.SSID.equals("twguest")) {
            filteredAps.add(ap);
//            }
        }
        signalServer.fetchCurrentLocation(filteredAps, new Callback<String>() {
            @Override
            public void success(String response, Response response2) {
                System.out.println("Fetch Success: " + response);
                locationNameView.setText(response);
                aps.clear();
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Fetch Error: " + error);
                locationNameView.setText("Failed to fetch location");
            }
        });
    }

    private void getCurrentLocation() {
        H.sendEmptyMessage(MSG_FETCH_WIFI_STRENGTH);
    }

    private SignalServer getSignalServer() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://10.132.124.51:9090")
                .build();
        return restAdapter.create(SignalServer.class);
    }
}
