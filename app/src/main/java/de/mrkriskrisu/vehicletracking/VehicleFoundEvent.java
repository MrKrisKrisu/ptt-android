package de.mrkriskrisu.vehicletracking;

import androidx.core.app.NotificationCompat;

public class VehicleFoundEvent {

    public static void trigger(Vehicle vehicle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.getInstance(), "Testchannel")
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle("Fahrzeug entdeckt")
                .setContentText("Fahrzeug " + vehicle.getVehicleName() + " entdeckt.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setTimeoutAfter(60 * 1000);

        MainActivity.getNotificationManager().notify((int) (Math.random() * 1000), builder.build());
    }

}
