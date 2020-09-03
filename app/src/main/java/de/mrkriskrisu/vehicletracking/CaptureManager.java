package de.mrkriskrisu.vehicletracking;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import de.mrkriskrisu.vehicletracking.tasks.ScanTask;

import static de.mrkriskrisu.vehicletracking.MainActivity.wifiManager;

public class CaptureManager implements View.OnClickListener {
    @NotNull
    private MainActivity mainActivity;

    public CaptureManager(@NotNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View v) {
        mainActivity.runOnUiThread(new ScanTask(mainActivity));
        //scanWifi();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            mainActivity.unregisterReceiver(this);

            System.out.println(results);
            System.out.println(results.size());

            JSONObject pushData = new JSONObject();

            for (ScanResult scanResult : results) {
                try {
                    pushData.put(scanResult.BSSID, new JSONObject()
                            .put("bssid", scanResult.BSSID)
                            .put("ssid", scanResult.SSID));
                } catch(JSONException ignored) {

                }
            }

            System.out.println(pushData.toString());

            String result = "Ein Fehler ist aufgetreten.";
            try {
                result = new WebRequest(new URL("https://verkehrstracking.de/entry/?trainID=" + mainActivity.inpBahnID.getText().toString() + "&catchMacQuery=" + URLEncoder.encode(pushData.toString())), "").doInBackground();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setMessage(result).setPositiveButton("OK", null);
            AlertDialog alert = builder.create();
            alert.show();
            mainActivity.buttonScan.setEnabled(true);
        }
    };

    private void scanWifi() {
        mainActivity.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(mainActivity, "Daten werden aufgenommen, bitte warten...", Toast.LENGTH_SHORT).show();
        mainActivity.buttonScan.setEnabled(false);
    }
}
