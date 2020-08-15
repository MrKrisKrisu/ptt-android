package de.mrkriskrisu.vehicletracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;

import com.evernote.android.job.JobManager;

import java.util.concurrent.TimeUnit;

import de.mrkriskrisu.vehicletracking.tasks.UploadWorker;

public class MainActivity extends AppCompatActivity {

    public static WifiManager wifiManager;
    public static Button buttonScan;
    public static Button buttonLocate;
    public static EditText inpBahnID;

    private static NotificationManagerCompat notificationManager;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        createNotificationChannel();

        inpBahnID = MainActivity.getInstance().findViewById(R.id.inpBahnID);

        buttonScan = findViewById(R.id.btnCapture);
        buttonScan.setOnClickListener(new CaptureManager());

        buttonLocate = findViewById(R.id.btnLocate);
        buttonLocate.setOnClickListener(new LocateManager());

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();

        //Regelmäßiger Cron zum Vehicle tracken
        /*
        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest.Builder(UploadWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueue(saveRequest);
        */

    }

    private void showPermissionPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setMessage("Die App benötigt Zugriff auf den Standort des Gerätes. Bitte erlaube den Zugriff in den Einstellungen.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void createNotificationChannel() {

        notificationManager = NotificationManagerCompat.from(this);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Testchannel";
            String description = "Testchannel Beschreibung";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Testchannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static NotificationManagerCompat getNotificationManager() {
        return notificationManager;
    }
}
