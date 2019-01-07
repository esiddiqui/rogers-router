package com.es.coda.rogersrouter.device;

public class Device {

    private String name;
    private String macAddress;
    private String ipAddress;
    private boolean managed;
    private boolean allowed;
    private String type;
    private String networkInterface;
    //private String managedDays;
    //private String managedHours;
    private DeviceAccessRestriction restrictions;

    public Device(String name, String macAddress, String ipAddress, boolean allowed) {
        this.name = name;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.allowed = allowed;
    }

    public Device(String name, String macAddress, String ipAddress, String type, String networkInterface, boolean managed, boolean allowed) {
        this.name = name;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.allowed = allowed;
        this.type = type;
        this.networkInterface = networkInterface;
        this.managed = managed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public boolean isManaged() {
        return managed;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }

//    public String getManagedDays() {
//        return managedDays;
//    }
//
//    public void setManagedDays(String managedDays) {
//        this.managedDays = managedDays;
//    }
//
//    public String getManagedHours() {
//        return managedHours;
//    }
//
//    public void setManagedHours(String managedHours) {
//        this.managedHours = managedHours;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNetworkInterface() {
        return networkInterface;
    }

    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }

    public DeviceAccessRestriction getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(DeviceAccessRestriction restrictions) {
        this.restrictions = restrictions;
    }
}
