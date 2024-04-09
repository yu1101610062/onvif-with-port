package com.yyzy.pz.onvif.discovery;

import com.yyzy.pz.onvif.soap.OnvifDevice;
import org.onvif.ver10.schema.Profile;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.net.URL;
import java.util.List;


public class OnvifPointer {
    private final String address;
    private final String name;
    private final String snapshotUrl;

    public String getSnapshotUrl() {
        return this.snapshotUrl;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public OnvifPointer(String address) {
        this.address = address;


        try {
            OnvifDevice device = new OnvifDevice(address);
            this.name = device.getName();
            List<Profile> profiles = device.getDevices().getProfiles();
            Profile profile = (Profile) profiles.get(0);
            this.snapshotUrl = device.getMedia().getSnapshotUri(profile.getToken());
        } catch (Exception var5) {
            throw new RuntimeException("no onvif device or device not configured", var5);
        }
    }

    public OnvifPointer(URL service) {
        this(service.getHost());
    }

    public OnvifDevice getOnvifDevice() throws SOAPException, ConnectException {
        return new OnvifDevice(this.address);
    }

    public String toString() {
        return "ONVIF: " + this.name + "@" + this.address;
    }
}
