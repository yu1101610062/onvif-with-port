package com.yyzy.pz.onvif.discovery;

import org.me.javawsdiscovery.DeviceDiscovery;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OnvifDiscovery {
    public OnvifDiscovery() {
    }

    public static List<OnvifPointer> discoverOnvifDevices() {
        ArrayList<OnvifPointer> onvifPointers = new ArrayList();
        Collection<URL> urls = DeviceDiscovery.discoverWsDevicesAsUrls("^http$", ".*onvif.*");
        Iterator i$ = urls.iterator();

        while(i$.hasNext()) {
            URL url = (URL)i$.next();

            try {
                onvifPointers.add(new OnvifPointer(url));
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        return onvifPointers;
    }
}
