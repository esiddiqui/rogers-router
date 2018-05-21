package com.es.coda.rogersrouter.device;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/devices")
public class DeviceController {


    @Autowired
    private DeviceService deviceService;

    @GetMapping("/")
    public List<Device> get() {
        System.out.println("GET /v1/devices/");
        return this.deviceService.findAll();
    }


    @GetMapping("/{name}")
    public Device get(@PathVariable("name") String name) {
        System.out.println("GET /v1/devices/"+name);
        return this.deviceService.findByName(name);
    }


    public void patch(Device device) {}

}
