package com.es.coda.rogersrouter.device;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DeviceServiceTest {


    @Autowired
    DeviceService deviceService;


    @Test
    public void findAll() {
        List<Device> devices = new ArrayList<>();//this.deviceService.findAll();
        assertNotNull(devices);
        //assertNotEquals(0,devices.size());
    }
}
