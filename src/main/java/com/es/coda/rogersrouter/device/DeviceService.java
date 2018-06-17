package com.es.coda.rogersrouter.device;

import java.util.List;

public interface DeviceService {

    List<Device>  findByName(String name);
    Device findByIp(String ipAddr);
    List<Device> findAll();
    boolean disableDevice(String name);

}
