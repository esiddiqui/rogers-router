package com.es.coda.rogersrouter.device;

public class DeviceAccessRestriction {

    private String managedDays;

    private String managedHours;

    public DeviceAccessRestriction(String managedDays, String managedHours) {
        this.managedDays = managedDays;
        this.managedHours = managedHours;
    }

    public String getManagedDays() {
        return managedDays;
    }

    public void setManagedDays(String managedDays) {
        this.managedDays = managedDays;
    }

    public String getManagedHours() {
        return managedHours;
    }

    public void setManagedHours(String managedHours) {
        this.managedHours = managedHours;
    }
}
