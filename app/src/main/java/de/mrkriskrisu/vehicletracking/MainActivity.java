package de.mrkriskrisu.vehicletracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
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

public class MainActivity extends AppCompatActivity {

    public static WifiManager wifiManager;
    public Button buttonScan;
    public Button buttonLocate;
    public EditText inpBahnID;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        createNotificationChannel();

        inpBahnID = this.findViewById(R.id.inpBahnID);

        buttonScan = findViewById(R.id.btnCapture);
        buttonScan.setOnClickListener(new CaptureManager(this));

        buttonLocate = findViewById(R.id.btnLocate);
        buttonLocate.setOnClickListener(new LocateManager(this));

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);

        //check that we have all necessary permission, if not prompt the user
        String[] permissions = new String[] {
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
        };
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                showPermissionPopup();
                break;
            }
        }

        //Regelmäßiger Cron zum Vehicle tracken
        /*
        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest.Builder(UploadWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueue(saveRequest);
        */

    }

    private void showPermissionPopup() {
        final Activity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Die App benötigt Zugriff auf den Standort des Gerätes. Bitte erlaube den Zugriff in den Einstellungen.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

    public NotificationManagerCompat getNotificationManager() {
        return notificationManager;
    }
}
