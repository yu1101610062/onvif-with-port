package com.yyzy.pz.onvif.soap.devices;

import com.yyzy.pz.onvif.soap.OnvifDevice;
import com.yyzy.pz.onvif.soap.SOAP;
import org.onvif.ver10.media.wsdl.*;
import org.onvif.ver10.schema.*;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.util.List;

public class OSDDevices {
    private SOAP soap;

    public OSDDevices(OnvifDevice onvifDevice) {
        this.soap = onvifDevice.getSoap();
    }

    public List<OSDConfiguration> getOSDs() {
        GetOSDs request = new GetOSDs();
        GetOSDsResponse response = new GetOSDsResponse();

        try {
            response = (GetOSDsResponse)this.soap.createSOAPMediaRequest(request, response, false);
        } catch (ConnectException | SOAPException var4) {
            var4.printStackTrace();
        }

        return response.getOSDs();
    }

    public String getVideoSourceToken(List<OSDConfiguration> list) {
        return list != null && list.size() > 0 ? ((OSDConfiguration)list.get(0)).getVideoSourceConfigurationToken().getValue() : null;
    }

    public String createOSD(String content, float x, float y, int colorX, int colorY, int colorZ, int fontSize, String videoSourceToken) {
        CreateOSD request = new CreateOSD();
        CreateOSDResponse response = new CreateOSDResponse();
        OSDConfiguration value = new OSDConfiguration();
        OSDReference oSDReference = new OSDReference();
        oSDReference.setValue(videoSourceToken);
        value.setVideoSourceConfigurationToken(oSDReference);
        value.setType(OSDType.TEXT);
        OSDPosConfiguration conf = new OSDPosConfiguration();
        Vector pos = new Vector();
        pos.setX(x);
        pos.setY(y);
        conf.setPos(pos);
        conf.setType("Custom");
        value.setPosition(conf);
        OSDTextConfiguration osdTestConf = new OSDTextConfiguration();
        osdTestConf.setPlainText(content);
        osdTestConf.setType("Plain");
        osdTestConf.setFontSize(fontSize);
        OSDColor fontColor = new OSDColor();
        Color color = new Color();
        color.setX((float)colorX);
        color.setY((float)colorY);
        color.setZ((float)colorZ);
        color.setColorspace("http://www.onvif.org/ver10/colorspace/YCbCr");
        fontColor.setColor(color);
        osdTestConf.setFontColor(fontColor);
        value.setTextString(osdTestConf);
        request.setOSD(value);

        try {
            response = (CreateOSDResponse)this.soap.createSOAPMediaRequest(request, response, false);
        } catch (ConnectException | SOAPException var19) {
            var19.printStackTrace();
            return null;
        }

        return response.getOSDToken();
    }

    public boolean setOSD(String content, float x, float y, int colorX, int colorY, int colorZ, int fontSize, String videoSourceToken, String osdToken) {
        SetOSD request = new SetOSD();
        SetOSDResponse response = new SetOSDResponse();
        OSDConfiguration value = new OSDConfiguration();
        value.setToken(osdToken);
        OSDReference oSDReference = new OSDReference();
        oSDReference.setValue(videoSourceToken);
        value.setVideoSourceConfigurationToken(oSDReference);
        value.setType(OSDType.TEXT);
        OSDPosConfiguration conf = new OSDPosConfiguration();
        Vector pos = new Vector();
        pos.setX(x);
        pos.setY(y);
        conf.setPos(pos);
        conf.setType("Custom");
        value.setPosition(conf);
        OSDTextConfiguration osdTestConf = new OSDTextConfiguration();
        osdTestConf.setPlainText(content);
        osdTestConf.setType("Plain");
        osdTestConf.setFontSize(24);
        OSDColor fontColor = new OSDColor();
        Color color = new Color();
        color.setX((float)colorX);
        color.setY((float)colorY);
        color.setZ((float)colorZ);
        color.setColorspace("http://www.onvif.org/ver10/colorspace/YCbCr");
        fontColor.setColor(color);
        osdTestConf.setFontColor(fontColor);
        value.setTextString(osdTestConf);
        request.setOSD(value);

        try {
            response = (SetOSDResponse)this.soap.createSOAPMediaRequest(request, response, false);
        } catch (ConnectException | SOAPException var20) {
            var20.printStackTrace();
            return false;
        }

        return response != null;
    }

    public boolean deleteOSD(String osdToken) {
        DeleteOSD request = new DeleteOSD();
        DeleteOSDResponse response = new DeleteOSDResponse();
        request.setOSDToken(osdToken);

        try {
            response = (DeleteOSDResponse)this.soap.createSOAPMediaRequest(request, response, false);
            return true;
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return false;
        }
    }
}
