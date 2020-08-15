package de.mrkriskrisu.vehicletracking.tasks;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import de.mrkriskrisu.vehicletracking.MainActivity;
import de.mrkriskrisu.vehicletracking.R;
import de.mrkriskrisu.vehicletracking.WebRequest;

import static de.mrkriskrisu.vehicletracking.MainActivity.wifiManager;

public class ScanTask implements Runnable {

    private Location resultLocation;

    @Override
    public void run() {
        final TextView tv = MainActivity.getInstance().findViewById(R.id.tv_captureResult);
        tv.setText("Signale werden empfangen...");


        Toast.makeText(MainActivity.getInstance(), "Daten werden aufgenommen, bitte warten...", Toast.LENGTH_SHORT).show();
        MainActivity.buttonScan.setEnabled(false);

        MainActivity.getInstance().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final List<ScanResult> resultWifi = wifiManager.getScanResults();
                MainActivity.getInstance().unregisterReceiver(this);

                System.out.println(resultWifi);
                System.out.println(resultWifi.size());

                tv.setText("Location wird abgefragt...");

                if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                    builder.setMessage("Fehler (L1)")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    tv.setText("Fehler");
                    return;
                }
                LocationServices.getFusedLocationProviderClient(MainActivity.getInstance()).getLastLocation()
                        .addOnSuccessListener(MainActivity.getInstance(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                tv.setText("Daten werden gespeichert...");

                                JSONObject pushData = new JSONObject();

                                for (ScanResult scanResult : resultWifi) {
                                    try {
                                        pushData.put(scanResult.BSSID, new JSONObject()
                                                .put("bssid", scanResult.BSSID)
                                                .put("ssid", scanResult.SSID));
                                    } catch (JSONException e) {

                                    }
                                }

                                System.out.println(pushData.toString());

                                String result = "Ein Fehler ist aufgetreten.";
                                try {
                                    String urlString = "https://wlan.dev.k118.de/entry/?trainID=" + MainActivity.getInstance().inpBahnID.getText().toString() + "&catchMacQuery=" + URLEncoder.encode(pushData.toString());
                                    if (location != null)
                                        urlString += "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude();
                                    result = new WebRequest(new URL(urlString), "").doInBackground();
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

                                tv.setText("");
                            }
                        });


            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();


    }

    private void scanWifi() {

    }

    private void scanLocation() {

    }
}
