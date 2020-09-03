package de.mrkriskrisu.vehicletracking;

import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;

public class VehicleFoundEvent {
    @NotNull
    private MainActivity mainActivity;

    public VehicleFoundEvent(@NotNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void trigger(Vehicle vehicle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity, "Testchannel")
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle("Fahrzeug entdeckt")
                .setContentText("Fahrzeug " + vehicle.getVehicleName() + " entdeckt.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setTimeoutAfter(60 * 1000);

        mainActivity.getNotificationManager().notify((int) (Math.random() * 1000), builder.build());
    }

}
