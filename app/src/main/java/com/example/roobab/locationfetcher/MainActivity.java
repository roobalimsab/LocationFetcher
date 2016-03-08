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
        setupWifiSignalReceivers();
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
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readSignalStrength();
        }
    };

    private void setupWifiSignalReceivers() {
        registerReceiver(wifiReceiver, new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        List<ParsedSignal> apsOfConcern = new ArrayList<>();
        for(ScanResult ap : aps) {
//            if(ap.SSID.equals("twguest")) {
            apsOfConcern.add(new ParsedSignal(ap.BSSID, ap.level));
//            }
        }
        signalServer.fetchCurrentLocation(new Gson().toJson(apsOfConcern), new Callback<String>() {
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
        unregisterReceivers();
    }

    private void getCurrentLocation() {
        H.sendEmptyMessage(MSG_FETCH_WIFI_STRENGTH);
    }

    private SignalServer getSignalServer() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://192.168.0.29:9090")
                .build();
        return restAdapter.create(SignalServer.class);
    }
}
