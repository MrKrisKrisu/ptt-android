package de.mrkriskrisu.vehicletracking;

public class Vehicle {

    private String bssid;
    private String vehicleName;
    private int companyId;

    public Vehicle(String bssid, String vehicleName, int companyId) {
        this.bssid = bssid;
        this.vehicleName = vehicleName;
        this.companyId = companyId;
    }

    public Vehicle(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getBSSID() {
        return this.bssid;
    }

    public String getVehicleName() {
        return this.vehicleName;
    }

    public int getCompanyID() {
        return this.companyId;
    }
}
