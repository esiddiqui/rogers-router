package com.es.coda.rogersrouter.device;

import com.es.utils.Scrapper;
import com.gargoylesoftware.htmlunit.html.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class HitronCoda4582DeviceService implements DeviceService {


    private static final String BASIC_LAN_SETUP_URL = "/index.html#basic_lan/m/2/s/0";

    private static final String SECURITY_DEVICE_FILTER_URL = "/index.html#security_macfilter/m/5/s/3";

    @Value("${router.host}")
    private String host;

    @Value("${router.username}")
    private String user;

    @Value("${router.password}")
    private String password;

    private Scrapper scrapper = new Scrapper();

    public List<Device> findByName(String name) {
        return this.list().stream()
                .filter(d->d.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public Device findByIp(String ipAddr) {
        return this.list().stream()
                .filter(d->d.getIpAddress().equalsIgnoreCase(ipAddr))
                .findFirst()
                .orElse(null);
    }

    public List<Device> findAll() {
        return this.list(false)
                .stream().collect(Collectors.toList());
    }

    public List<Device> findManaged() {
        return this.list(true)
                .stream().collect(Collectors.toList());
    }


    /**
     * Creates a list of all connected devices
     * offline devices & managed devices are not
     * included if not listed by the router software
     *
     * @return a List of device objects
     */
    private List<Device> list (boolean managedOnly) {
        
        List<Device> devices = new ArrayList<>();
        Hashtable<String,DeviceAccessRestriction> accessControlledDeviceHash = new Hashtable<>();

        try {
            String host2="http://" +this.host;
            HtmlPage page = this.scrapper.setOptions().setOptionJavaScriptWaitTimeDefault(3000)
                .visit(host2 + "/login.html")
                .inputTextBoxX("//input[@id='user_login']",this.user)
                .passwordTextBoxX("//input[@id='user_password']",this.password)
                .clickSubmitX("//button[@id='btnLogin']",3000)
                .visit(host2+ "/index.html")
                .validateElementX("//a[@id='btnLogout']")
                .visit(host2+SECURITY_DEVICE_FILTER_URL,1500).getCurrentPage();

            HtmlTable table = null;
            List<HtmlTableRow> rows = null;

            //access control status
            DeviceManageType manageType = DeviceManageType.ALLOW_ALL;
            List<DomElement> buttons = page.getElementsByName("blockType");
            DomElement ele = buttons.stream()
                    .filter(btn -> btn.getAttribute("class")
                            .indexOf("btn-primary") != -1).findFirst().get();
            String value = ((HtmlButton) ele).getAttribute("value");
            if (value.equalsIgnoreCase("Block Listed"))
                manageType = DeviceManageType.BLOCK_MANAGED; //the list below is blocked
            else if (value.equalsIgnoreCase("Allow Listed"))
                manageType = DeviceManageType.ALLOW_MANAGED; // the list above is blocked
            final DeviceManageType finalManagedType = manageType;

            //access controlled devices
            String xpathDisabled = "//*[@id='macFilterMain']/div[4]/table";
            table = (HtmlTable) page.getFirstByXPath(xpathDisabled);

            rows = table.getRows();
            rows.stream().forEach(row -> {
                if (row.getCells().size() > 3 &&
                        !row.getCell(0).getTextContent().trim().equalsIgnoreCase("Host Name")) {
                    Device device = new Device(
                            row.getCell(0).getTextContent().trim(),
                            row.getCell(1).getTextContent().trim(),
                            "NOT ASSIGNED", "N/A", "N/A",
                            true,
                            finalManagedType != DeviceManageType.BLOCK_MANAGED);
                    String macAddr = row.getCell(1).getTextContent().trim();
                    String managedDays = row.getCell(2).getTextContent().trim();
                    String managedHours = row.getCell(3).getTextContent().trim();
                    DeviceAccessRestriction restr = new DeviceAccessRestriction(managedDays, managedHours);
                    accessControlledDeviceHash.put(macAddr, restr);
                    device.setRestrictions(restr);
                    devices.add(device);
                }
            });

            /* if managed devices are requested */
            if (managedOnly)
             return devices;

            //Get list of all devices currently attached from Basic->LAN Setup page
            this.scrapper.setOptions().setOptionJavaScriptWaitTimeDefault(3000)
                    //.visit(host2+BASIC_LAN_SETUP_URL,1500)
                    .clickSubmit("btnShowConnectedDevice", 5500)
                    .getCurrentPage();

            String connectedDevicesHtmlTableId = "tblConnectedDevice";
            table = (HtmlTable) page.getElementById(connectedDevicesHtmlTableId);
            rows = table.getRows();
            rows.stream().forEach(row -> {
                if (row.getCells().size() > 3 &&
                        !row.getCell(0).getTextContent().trim().equalsIgnoreCase("Host Name")) {

                    String macAddr = row.getCell(2).getTextContent().trim();
                    boolean allowed = true;
                    if (finalManagedType == DeviceManageType.BLOCK_MANAGED &&
                            accessControlledDeviceHash.containsKey(macAddr))
                        allowed = false;
                    else if (finalManagedType == DeviceManageType.ALLOW_MANAGED &&
                            !accessControlledDeviceHash.containsKey(macAddr))
                        allowed = false;

                    Device device = new Device(
                            row.getCell(0).getTextContent().trim(),
                            macAddr,
                            row.getCell(1).getTextContent().trim(),
                            row.getCell(3).getTextContent().trim(),
                            row.getCell(4).getTextContent().trim(),
                            accessControlledDeviceHash.containsKey(macAddr),
                            allowed
                    );
                    devices.add(device);
                }
            });
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        return devices;
    }


    public boolean disableDevice(String name) {
        return false;
    }


    public static enum DeviceManageType {
        ALLOW_ALL, ALLOW_MANAGED, BLOCK_MANAGED
    }


}
