package com.yyzy.pz.onvif.soap.devices;

import com.yyzy.pz.onvif.soap.OnvifDevice;
import com.yyzy.pz.onvif.soap.SOAP;
import org.onvif.ver10.schema.AbsoluteFocus;
import org.onvif.ver10.schema.FocusMove;
import org.onvif.ver10.schema.ImagingOptions20;
import org.onvif.ver10.schema.ImagingSettings20;
import org.onvif.ver20.imaging.wsdl.*;


import javax.xml.soap.SOAPException;
import java.net.ConnectException;

public class ImagingDevices {
    private OnvifDevice onvifDevice;
    private SOAP soap;

    public ImagingDevices(OnvifDevice onvifDevice) {
        this.onvifDevice = onvifDevice;
        this.soap = onvifDevice.getSoap();
    }

    public ImagingOptions20 getOptions(String videoSourceToken) {
        if (videoSourceToken == null) {
            return null;
        } else {
            GetOptions request = new GetOptions();
            GetOptionsResponse response = new GetOptionsResponse();
            request.setVideoSourceToken(videoSourceToken);

            try {
                response = (GetOptionsResponse)this.soap.createSOAPImagingRequest(request, response, false);
            } catch (ConnectException | SOAPException var5) {
                var5.printStackTrace();
                return null;
            }

            return response == null ? null : response.getImagingOptions();
        }
    }

    public boolean moveFocus(String videoSourceToken, float absoluteFocusValue) {
        if (videoSourceToken == null) {
            return false;
        } else {
            Move request = new Move();
            MoveResponse response = new MoveResponse();
            AbsoluteFocus absoluteFocus = new AbsoluteFocus();
            absoluteFocus.setPosition(absoluteFocusValue);
            FocusMove focusMove = new FocusMove();
            focusMove.setAbsolute(absoluteFocus);
            request.setVideoSourceToken(videoSourceToken);
            request.setFocus(focusMove);

            try {
                response = (MoveResponse)this.soap.createSOAPImagingRequest(request, response, true);
            } catch (ConnectException | SOAPException var8) {
                var8.printStackTrace();
                return false;
            }

            return response != null;
        }
    }

    public ImagingSettings20 getImagingSettings(String videoSourceToken) {
        if (videoSourceToken == null) {
            return null;
        } else {
            GetImagingSettings request = new GetImagingSettings();
            GetImagingSettingsResponse response = new GetImagingSettingsResponse();
            request.setVideoSourceToken(videoSourceToken);

            try {
                response = (GetImagingSettingsResponse)this.soap.createSOAPImagingRequest(request, response, true);
            } catch (ConnectException | SOAPException var5) {
                var5.printStackTrace();
                return null;
            }

            return response == null ? null : response.getImagingSettings();
        }
    }

    public boolean setImagingSettings(String videoSourceToken, ImagingSettings20 imagingSettings) {
        if (videoSourceToken == null) {
            return false;
        } else {
            SetImagingSettings request = new SetImagingSettings();
            SetImagingSettingsResponse response = new SetImagingSettingsResponse();
            request.setVideoSourceToken(videoSourceToken);
            request.setImagingSettings(imagingSettings);

            try {
                response = (SetImagingSettingsResponse)this.soap.createSOAPImagingRequest(request, response, true);
            } catch (ConnectException | SOAPException var6) {
                var6.printStackTrace();
                return false;
            }

            return response != null;
        }
    }
}
