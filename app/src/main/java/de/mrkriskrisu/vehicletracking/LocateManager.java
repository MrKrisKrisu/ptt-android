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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static de.mrkriskrisu.vehicletracking.MainActivity.wifiManager;


public class LocateManager implements View.OnClickListener {
    @NotNull
    private MainActivity mainActivity;
    @NotNull
    private VehicleFoundEvent vehicleFoundEvent;

    public LocateManager(@NotNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.vehicleFoundEvent = new VehicleFoundEvent(this.mainActivity);
    }

    @Override
    public void onClick(View v) {
        ProgressBar spinner = mainActivity.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        mainActivity.buttonLocate.setEnabled(false);

        mainActivity.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(mainActivity, "Fahrzeuge werden lokalisiert...", Toast.LENGTH_SHORT).show();
    }

    final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.unregisterReceiver(this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                                builder.setMessage("Es konnten keine Fahrzeuge mit 100%iger Wahrscheinlichkeit identifiert werden.")
                                        .setPositiveButton("OK", null);
                                builder.create().show();
                                return;
                            }

                            StringBuilder message = new StringBuilder();
                            if (verified.length() > 0) {
                                message.append("Folgende Fahrzeuge wurde eindeutig gefunden: \n");
                                for (int i = 0; i < verified.length(); i++) {
                                    message.append(" - ").append(verified.get(i).toString()).append("\n");

                                    vehicleFoundEvent.trigger(new Vehicle(verified.get(i).toString()));
                                }
                            }

                            if (verified.length() > 0 && possible.length() > 0)
                                message.append("\n\n");

                            if (possible.length() > 0) {
                                message.append("Folgende Fahrzeuge könnten noch in deiner Umgebung sein: \n");
                                for (int i = 0; i < possible.length(); i++) {
                                    message
                                            .append(" - ")
                                            .append(possible.get(i).toString())
                                            .append("\n");
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                            builder.setMessage(message).setPositiveButton("OK", null).create().show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        ProgressBar spinner = mainActivity.findViewById(R.id.progressBar);
                        spinner.setVisibility(View.GONE);
                        mainActivity.buttonLocate.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
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
