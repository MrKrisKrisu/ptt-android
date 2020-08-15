package de.mrkriskrisu.vehicletracking;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static de.mrkriskrisu.vehicletracking.MainActivity.wifiManager;


public class LocateManager implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        MainActivity.getInstance().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(MainActivity.getInstance(), "Fahrzeuge werden lokalisiert...", Toast.LENGTH_SHORT).show();
        MainActivity.buttonLocate.setEnabled(false);
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.getInstance().unregisterReceiver(this);
            try {
                String jsonQuery = parseJsonRequest(wifiManager.getScanResults());

                AsyncHttpClient client = new AsyncHttpClient();
                client.post(null, "https://vehicletracking.de/api/vehicle/locate", new StringEntity(jsonQuery), "application/json", new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        System.out.println("onSuccess");
                        try {
                            JSONArray verified = (JSONArray) response.get("verified");
                            JSONArray possible = (JSONArray) response.get("possible");

                            if (verified.length() == 0 && possible.length() == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                                builder.setMessage("Es konnten keine Fahrzeuge mit 100%iger Wahrscheinlichkeit identifiert werden.")
                                        .setPositiveButton("OK", null);
                                builder.create().show();
                                return;
                            }

                            StringBuilder message = new StringBuilder();
                            if (verified.length() > 0) {
                                message.append("Folgende Fahrzeuge wurde eindeutig gefunden: \n");
                                for (int i = 0; i < verified.length(); i++) {
                                    message.append(" - " + verified.get(i).toString() + "\n");

                                    VehicleFoundEvent.trigger(new Vehicle(verified.get(i).toString()));
                                }
                            }

                            if (verified.length() > 0 && possible.length() > 0)
                                message.append("\n\n");

                            if (possible.length() > 0) {
                                message.append("Folgende Fahrzeuge könnten noch in deiner Umgebung sein: \n");
                                for (int i = 0; i < possible.length(); i++) {
                                    message.append(" - ");
                                    message.append(possible.get(i).toString() + "\n");
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                            builder.setMessage(message).setPositiveButton("OK", null).create().show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        MainActivity.buttonLocate.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    };

    private String parseJsonRequest(List<ScanResult> scan) throws JSONException {
        JSONArray array = new JSONArray();

        for (ScanResult scanResult : scan) {
            Log.d("ScanResult", scanResult.toString());
            array.put(scanResult.BSSID);
        }

        return array.toString(0);
    }
}
