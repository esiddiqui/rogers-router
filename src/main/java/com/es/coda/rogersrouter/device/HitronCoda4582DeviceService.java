package com.es.coda.rogersrouter.device;

import com.es.utils.Scrapper;
import com.gargoylesoftware.htmlunit.html.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class HitronCoda4582DeviceService implements DeviceService {


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
        return this.list().stream().collect(Collectors.toList());
    }


    private List<Device> list () {
        List<Device> devices = new ArrayList<>();

        try {
            String host2="http://" +this.host;
            HtmlPage page =
                this.scrapper.setOptions().setOptionJavaScriptWaitTimeDefault(3000)
                    .visit(host2 + "/login.html")
                    .inputTextBoxX("//input[@id='user_login']",this.user)
                    .passwordTextBoxX("//input[@id='user_password']",this.password)
                    .clickSubmitX("//button[@id='btnLogin']",3000)
                    .visit(host2+ "/index.html")
                    .validateElementX("//a[@id='btnLogout']")
                    .visit(host2+"/index.html#security_macfilter/m/5/s/3",1500)//.visit(host2+"/index.html#basic_lan/m/2/s/0",1500)
                    .clickSubmit("btnShowConnectedDevice",5500)
                    .getCurrentPage();


                HtmlTable table = (HtmlTable) page.getElementById("tblConnectedDevice");


                //access control status
                List<DomElement> buttons = page.getElementsByName("blockType");
                DomElement ele = buttons.stream().filter(btn->btn.getAttribute("class").indexOf("btn-primary")!=-1).findFirst().get();
                String value = ((HtmlButton)ele).getAttribute("value");
                int accessControlValue=0;
                if (value.equalsIgnoreCase("Block Listed"))
                    accessControlValue=1; /* the list below is blocked */
                else if (value.equalsIgnoreCase("Allow Listed"))
                    accessControlValue=-1; /* the list above is blocked */

                final int accessControlValue2=accessControlValue;

                //connected & available devices

                List<HtmlTableRow> rows = table.getRows();
                rows.stream().forEach(row->{
                    if (row.getCells().size()>3 &&
                            !row.getCell(0).getTextContent().trim().equalsIgnoreCase("Host Name")) {
                        Device device = new Device(
                                row.getCell(0).getTextContent().trim(),
                                row.getCell(2).getTextContent().trim(),
                                row.getCell(1).getTextContent().trim(),
                                row.getCell(3).getTextContent().trim(),
                                row.getCell(4).getTextContent().trim(),
                                false,
                                accessControlValue2!=-1
                                //row.getCell(5).getTextContent().trim().equalsIgnoreCase("Active")
                                );
                        devices.add(device);
                    }
                });

               //access controlled devices
                String xpathDisabled = "//*[@id='macFilterMain']/div[4]/table";
                table  = (HtmlTable) page.getFirstByXPath(xpathDisabled);

                rows = table.getRows();
                rows.stream().forEach(row->{
                    if (row.getCells().size()>3 &&
                            !row.getCell(0).getTextContent().trim().equalsIgnoreCase("Host Name")) {
                        Device device = new Device(
                                row.getCell(0).getTextContent().trim(),
                                row.getCell(1).getTextContent().trim(),
                                "","","",
                                true,
                                accessControlValue2!=1);
                        device.setManagedDays(row.getCell(2).getTextContent().trim());
                        device.setManagedHours(row.getCell(3).getTextContent().trim());

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



}
