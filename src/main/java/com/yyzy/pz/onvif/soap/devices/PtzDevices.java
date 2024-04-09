package com.yyzy.pz.onvif.soap.devices;

import com.yyzy.pz.onvif.soap.OnvifDevice;
import com.yyzy.pz.onvif.soap.SOAP;
import org.onvif.ver10.schema.*;
import org.onvif.ver20.ptz.wsdl.*;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.util.List;

public class PtzDevices {
    private OnvifDevice onvifDevice;
    private SOAP soap;

    public PtzDevices(OnvifDevice onvifDevice) {
        this.onvifDevice = onvifDevice;
        this.soap = onvifDevice.getSoap();
    }

    public boolean isPtzOperationsSupported(String profileToken) {
        return this.getPTZConfiguration(profileToken) != null;
    }

    public PTZConfiguration getPTZConfiguration(String profileToken) {
        if (profileToken != null && !profileToken.equals("")) {
            Profile profile = this.onvifDevice.getDevices().getProfile(profileToken);
            if (profile == null) {
                throw new IllegalArgumentException("No profile available for token: " + profileToken);
            } else {
                return profile.getPTZConfiguration() == null ? null : profile.getPTZConfiguration();
            }
        } else {
            return null;
        }
    }

    public List<PTZNode> getNodes() {
        GetNodes request = new GetNodes();
        GetNodesResponse response = new GetNodesResponse();

        try {
            response = (GetNodesResponse)this.soap.createSOAPDeviceRequest(request, response, true);
        } catch (ConnectException | SOAPException var4) {
            var4.printStackTrace();
            return null;
        }

        return response == null ? null : response.getPTZNode();
    }

    public PTZNode getNode(String profileToken) {
        return this.getNode(this.getPTZConfiguration(profileToken));
    }

    public PTZNode getNode(PTZConfiguration ptzConfiguration) {
        GetNode request = new GetNode();
        GetNodeResponse response = new GetNodeResponse();
        if (ptzConfiguration == null) {
            return null;
        } else {
            request.setNodeToken(ptzConfiguration.getNodeToken());

            try {
                response = (GetNodeResponse)this.soap.createSOAPDeviceRequest(request, response, true);
            } catch (ConnectException | SOAPException var5) {
                var5.printStackTrace();
                return null;
            }

            return response == null ? null : response.getPTZNode();
        }
    }

    public FloatRange getPanSpaces(String profileToken) {
        PTZNode node = this.getNode(profileToken);
        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ((Space2DDescription)ptzSpaces.getAbsolutePanTiltPositionSpace().get(0)).getXRange();
    }

    public FloatRange getTiltSpaces(String profileToken) {
        PTZNode node = this.getNode(profileToken);
        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ((Space2DDescription)ptzSpaces.getAbsolutePanTiltPositionSpace().get(0)).getYRange();
    }

    public FloatRange getZoomSpaces(String profileToken) {
        PTZNode node = this.getNode(profileToken);
        PTZSpaces ptzSpaces = node.getSupportedPTZSpaces();
        return ((Space1DDescription)ptzSpaces.getAbsoluteZoomPositionSpace().get(0)).getXRange();
    }

    public boolean isAbsoluteMoveSupported(String profileToken) {
        Profile profile = this.onvifDevice.getDevices().getProfile(profileToken);

        try {
            if (profile.getPTZConfiguration().getDefaultAbsolutePantTiltPositionSpace() != null) {
                return true;
            }
        } catch (NullPointerException var4) {
        }

        return false;
    }

    public boolean absoluteMove(String profileToken, float x, float y, float zoom) throws SOAPException {
        PTZNode node = this.getNode(profileToken);
        if (node != null) {
            FloatRange xRange = ((Space2DDescription)node.getSupportedPTZSpaces().getAbsolutePanTiltPositionSpace().get(0)).getXRange();
            FloatRange yRange = ((Space2DDescription)node.getSupportedPTZSpaces().getAbsolutePanTiltPositionSpace().get(0)).getYRange();
            FloatRange zRange = ((Space1DDescription)node.getSupportedPTZSpaces().getAbsoluteZoomPositionSpace().get(0)).getXRange();
            if (zoom < zRange.getMin() || zoom > zRange.getMax()) {
                throw new IllegalArgumentException("Bad value for zoom: " + zoom);
            }

            if (x < xRange.getMin() || x > xRange.getMax()) {
                throw new IllegalArgumentException("Bad value for pan:/x " + x);
            }

            if (y < yRange.getMin() || y > yRange.getMax()) {
                throw new IllegalArgumentException("Bad value for tilt/y: " + y);
            }
        }

        AbsoluteMove request = new AbsoluteMove();
        AbsoluteMoveResponse response = new AbsoluteMoveResponse();
        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(x);
        panTiltVector.setY(y);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(zoom);
        PTZVector ptzVector = new PTZVector();
        ptzVector.setPanTilt(panTiltVector);
        ptzVector.setZoom(zoomVector);
        request.setPosition(ptzVector);
        request.setProfileToken(profileToken);

        try {
            response = (AbsoluteMoveResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (SOAPException var12) {
            throw var12;
        } catch (ConnectException var13) {
            var13.printStackTrace();
            return false;
        }

        if (response == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isRelativeMoveSupported(String profileToken) {
        Profile profile = this.onvifDevice.getDevices().getProfile(profileToken);

        try {
            if (profile.getPTZConfiguration().getDefaultRelativePanTiltTranslationSpace() != null) {
                return true;
            }
        } catch (NullPointerException var4) {
        }

        return false;
    }

    public boolean relativeMove(String profileToken, float x, float y, float zoom) {
        RelativeMove request = new RelativeMove();
        RelativeMoveResponse response = new RelativeMoveResponse();
        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(x);
        panTiltVector.setY(y);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(zoom);
        PTZVector translation = new PTZVector();
        translation.setPanTilt(panTiltVector);
        translation.setZoom(zoomVector);
        request.setProfileToken(profileToken);
        request.setTranslation(translation);

        try {
            response = (RelativeMoveResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (ConnectException | SOAPException var11) {
            var11.printStackTrace();
            return false;
        }

        return response != null;
    }

    public boolean isContinuosMoveSupported(String profileToken) {
        Profile profile = this.onvifDevice.getDevices().getProfile(profileToken);

        try {
            if (profile.getPTZConfiguration().getDefaultContinuousPanTiltVelocitySpace() != null) {
                return true;
            }
        } catch (NullPointerException var4) {
        }

        return false;
    }

    public boolean continuousMove(String profileToken, float x, float y, float zoom) {
        ContinuousMove request = new ContinuousMove();
        ContinuousMoveResponse response = new ContinuousMoveResponse();
        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(x);
        panTiltVector.setY(y);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(zoom);
        PTZSpeed ptzSpeed = new PTZSpeed();
        ptzSpeed.setPanTilt(panTiltVector);
        ptzSpeed.setZoom(zoomVector);
        request.setVelocity(ptzSpeed);
        request.setProfileToken(profileToken);

        try {
            response = (ContinuousMoveResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (ConnectException | SOAPException var11) {
            var11.printStackTrace();
            return false;
        }

        return response != null;
    }

    public boolean stopMove(String profileToken) {
        Stop request = new Stop();
        request.setPanTilt(true);
        request.setZoom(true);
        StopResponse response = new StopResponse();
        request.setProfileToken(profileToken);

        try {
            response = (StopResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return false;
        }

        return response != null;
    }

    public PTZStatus getStatus(String profileToken) {
        GetStatus request = new GetStatus();
        GetStatusResponse response = new GetStatusResponse();
        request.setProfileToken(profileToken);

        try {
            response = (GetStatusResponse)this.soap.createSOAPPtzRequest(request, response, false);
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return null;
        }

        return response == null ? null : response.getPTZStatus();
    }

    public PTZVector getPosition(String profileToken) {
        PTZStatus status = this.getStatus(profileToken);
        return status == null ? null : status.getPosition();
    }

    public boolean setHomePosition(String profileToken) {
        SetHomePosition request = new SetHomePosition();
        SetHomePositionResponse response = new SetHomePositionResponse();
        request.setProfileToken(profileToken);

        try {
            response = (SetHomePositionResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return false;
        }

        return response != null;
    }

    public List<PTZPreset> getPresets(String profileToken) {
        GetPresets request = new GetPresets();
        GetPresetsResponse response = new GetPresetsResponse();
        request.setProfileToken(profileToken);

        try {
            response = (GetPresetsResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return null;
        }

        return response == null ? null : response.getPreset();
    }

    public String setPreset(String presetName, String presetToken, String profileToken) {
        SetPreset request = new SetPreset();
        SetPresetResponse response = new SetPresetResponse();
        request.setProfileToken(profileToken);
        request.setPresetName(presetName);
        request.setPresetToken(presetToken);

        try {
            response = (SetPresetResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (ConnectException | SOAPException var7) {
            var7.printStackTrace();
            return null;
        }

        return response == null ? null : response.getPresetToken();
    }

    public String setPreset(String presetName, String profileToken) {
        return this.setPreset(presetName, (String)null, profileToken);
    }

    public boolean removePreset(String presetToken, String profileToken) {
        RemovePreset request = new RemovePreset();
        RemovePresetResponse response = new RemovePresetResponse();
        request.setProfileToken(profileToken);
        request.setPresetToken(presetToken);

        try {
            response = (RemovePresetResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (ConnectException | SOAPException var6) {
            var6.printStackTrace();
            return false;
        }

        return response != null;
    }

    public boolean gotoPreset(String presetToken, String profileToken) {
        GotoPreset request = new GotoPreset();
        GotoPresetResponse response = new GotoPresetResponse();
        request.setProfileToken(profileToken);
        request.setPresetToken(presetToken);

        try {
            response = (GotoPresetResponse)this.soap.createSOAPPtzRequest(request, response, true);
        } catch (ConnectException | SOAPException var6) {
            var6.printStackTrace();
            return false;
        }

        return response != null;
    }
}
