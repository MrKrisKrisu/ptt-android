package de.mrkriskrisu.vehicletracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static WifiManager wifiManager;
    public static Button buttonScan;
    public static Button buttonLocate;
    public static EditText inpBahnID;

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            showPermissionPopup();
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
}
