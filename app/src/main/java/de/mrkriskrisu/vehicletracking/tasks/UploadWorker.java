package de.mrkriskrisu.vehicletracking.tasks;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
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

public class UploadWorker extends Worker {

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
/*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.getInstance(), "Testchannel")
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle("Start scan")
                .setContentText("s")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setTimeoutAfter(10 * 1000);
        MainActivity.getNotificationManager().notify((int) (Math.random() * 1000), builder.build());

        MainActivity.getInstance().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final List<ScanResult> resultWifi = wifiManager.getScanResults();
                MainActivity.getInstance().unregisterReceiver(this);

                System.out.println(resultWifi);
                System.out.println(resultWifi.size());

                if (ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.getInstance(), "Testchannel")
                            .setSmallIcon(R.drawable.common_full_open_on_phone)
                            .setContentTitle("Scan - Fehler L1")
                            .setContentText("s")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            //.setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setTimeoutAfter(10 * 1000);
                    MainActivity.getNotificationManager().notify((int) (Math.random() * 1000), builder.build());

                    return;
                }
                LocationServices.getFusedLocationProviderClient(MainActivity.getInstance()).getLastLocation()
                        .addOnSuccessListener(MainActivity.getInstance(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
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
                                    String urlString = "https://wlan.dev.k118.de/entry/?trainID=,.u&catchMacQuery=" + URLEncoder.encode(pushData.toString());
                                    if (location != null)
                                        urlString += "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude();
                                    result = new WebRequest(new URL(urlString), "").doInBackground();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.getInstance(), "Testchannel")
                                        .setSmallIcon(R.drawable.common_full_open_on_phone)
                                        .setContentTitle("Scan beendet")
                                        .setContentText(result)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        // Set the intent that will fire when the user taps the notification
                                        //.setContentIntent(pendingIntent)
                                        .setAutoCancel(true)
                                        .setTimeoutAfter(20 * 60 * 1000);
                                MainActivity.getNotificationManager().notify((int) (Math.random() * 1000), builder.build());
                                MainActivity.buttonScan.setEnabled(true);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.getInstance(), "Testchannel")
                                        .setSmallIcon(R.drawable.common_full_open_on_phone)
                                        .setContentTitle("Scan mit Fehler beendet")
                                        .setContentText("Die Location konnte nicht abgefragt werden.")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        // Set the intent that will fire when the user taps the notification
                                        //.setContentIntent(pendingIntent)
                                        .setAutoCancel(true)
                                        .setTimeoutAfter(20 * 60 * 1000);
                                MainActivity.getNotificationManager().notify((int) (Math.random() * 1000), builder.build());
                            }
                        });


            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();

*/
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}