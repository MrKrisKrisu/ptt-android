package de.mrkriskrisu.vehicletracking;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import static de.mrkriskrisu.vehicletracking.MainActivity.wifiManager;


public class LocateManager implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        scanWifi();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            MainActivity.getInstance().unregisterReceiver(this);

            String query = "";

            for (ScanResult scanResult : results) {
                System.out.println(scanResult);
                query += '"' + scanResult.BSSID + '"';
            }

            String jsonQuery = "[" + query.replaceAll("\"\"", "\", \"") + "]";

            String result = null;
            try {
                result = new WebRequest(new URL("https://ptt.dev.k118.de/api/vehicle/locate"), jsonQuery).doInBackground();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (result == null) {
                //Todo: fehler
            } else {
                String message = "";
                try {
                    JSONObject reader = new JSONObject(result);
                    JSONArray verified = reader.getJSONArray("verified");
                    JSONArray possible = reader.getJSONArray("possible");

                    if (verified.length() > 0) {
                        message += "Folgende Fahrzeuge befinden sich gerade in deiner Umgebung: \r\n";
                        for (int i = 0; i < verified.length(); i++) {
                            String s1 = verified.get(i).toString();
                            message += "- " + s1 + "\r\n";
                        }
                        message += "\r\n";
                    } else {
                        message += "Es konnten keine Fahrzeuge mit 100%iger Wahrscheinlichkeit identifiert werden.";
                        message += "\r\n";
                        message += "\r\n";
                    }

                    if (possible.length() > 0) {
                        message += "Folgende Fahrzeuge könnten sich in deiner Umgebung befinden: \r\n";
                        for (int i = 0; i < possible.length(); i++) {
                            String s1 = possible.get(i).toString();
                            message += "- " + s1 + "\r\n";
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (message.length() == 0) message = "Es ist ein Fehler aufgetreten! :(";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            MainActivity.buttonLocate.setEnabled(true);
        }
    };

    private void scanWifi() {
        MainActivity.getInstance().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(MainActivity.getInstance(), "Fahrzeuge werden lokalisiert...", Toast.LENGTH_SHORT).show();
        MainActivity.buttonLocate.setEnabled(false);
    }
}
