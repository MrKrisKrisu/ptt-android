package de.mrkriskrisu.vehicletracking;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static de.mrkriskrisu.vehicletracking.MainActivity.wifiManager;

public class CaptureManager implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        scanWifi();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            MainActivity.getInstance().unregisterReceiver(this);

            System.out.println(results);
            System.out.println(results.size());

            String query = "";

            JSONObject pushData = new JSONObject();

            for (ScanResult scanResult : results) {
                try {
                    pushData.put(scanResult.BSSID, new JSONObject()
                            .put("bssid", scanResult.BSSID)
                            .put("ssid", scanResult.SSID));
                } catch(JSONException e) {

                }

                query += "{\"bssid\": \""+scanResult.BSSID + "\", \"ssid\": \""+ URLEncoder.encode(scanResult.SSID) +"\"}";
            }

            System.out.println(pushData.toString());

            String result = "Ein Fehler ist aufgetreten.";
            try {
                result = new WebRequest(new URL("https://wlan.dev.k118.de/entry/?trainID=" + MainActivity.getInstance().inpBahnID.getText().toString() + "&catchMacQuery=" + URLEncoder.encode(pushData.toString()))).doInBackground();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
            builder.setMessage(result)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            MainActivity.buttonScan.setEnabled(true);
        };
    };

    private void scanWifi() {
        MainActivity.getInstance().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(MainActivity.getInstance(), "Daten werden aufgenommen, bitte warten...", Toast.LENGTH_SHORT).show();
        MainActivity.buttonScan.setEnabled(false);
    }
}
