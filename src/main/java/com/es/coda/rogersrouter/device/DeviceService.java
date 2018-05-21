package com.es.coda.rogersrouter.device;

import java.util.List;

public interface DeviceService {

    Device findByName(String name);
    List<Device> findAll();
    boolean disableDevice(String name);

}
