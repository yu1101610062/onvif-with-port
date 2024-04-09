package com.yyzy.pz;



import com.yyzy.pz.onvif.soap.OnvifDevice;
import com.yyzy.pz.onvif.soap.devices.OSDDevices;
import com.yyzy.pz.onvif.soap.devices.PtzDevices;
import org.onvif.ver10.device.wsdl.GetDeviceInformationResponse;
import org.onvif.ver10.schema.OSDConfiguration;
import org.onvif.ver10.schema.Profile;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OnvifAPI {
    public static final int Move_Direction_Up = 1;
    public static final int Move_Direction_Down = 2;
    public static final int Move_Direction_Left = 3;
    public static final int Move_Direction_Right = 4;
    private OnvifDevice nvt;
    private PtzDevices ptz;
    private OSDDevices osd;
    private String profileToken;
    private String videoSourceToken;

    public OnvifAPI(String hostIp, int port, String username, String password) throws ConnectException, SOAPException {
        this.nvt = new OnvifDevice(hostIp, port, username, password);
    }

    public GetDeviceInformationResponse getDeviceInformation() {
        return this.nvt.getDevices().getDeviceInformation();
    }

    public List<OSDConfiguration> getOSDs() {
        this.setOSD();
        List<OSDConfiguration> osDs = this.osd.getOSDs();
        this.setVideoSourceToken();
        return osDs;
    }

    public String createOSD(String content, float x, float y, int colorX, int colorY, int colorZ, int fontSize) {
        x = (x - 50.0F) / 50.0F;
        y = -(y - 50.0F) / 50.0F;
        this.setOSD();
        this.setVideoSourceToken();
        return this.osd.createOSD(content, x, y, 16, 128, 128, fontSize, this.videoSourceToken);
    }

    public boolean updateOSD(String osdToken, String content, float x, float y, int colorX, int colorY, int colorZ, int fontSize) {
        x = (x - 50.0F) / 50.0F;
        y = -(y - 50.0F) / 50.0F;
        this.setOSD();
        this.setVideoSourceToken();
        return this.osd.setOSD(content, x, y, 16, 128, 128, fontSize, this.videoSourceToken, osdToken);
    }

    public boolean deleteOSD(String osdToken) {
        this.setOSD();
        return this.osd.deleteOSD(osdToken);
    }

    public String createPreset(String presetName) {
        this.setPtz();
        this.setProfileToken();
        String presetToken = this.ptz.setPreset(presetName, this.profileToken);
        return presetToken;
    }

    public String updatePreset(String presetName, String presetToken) {
        this.setPtz();
        this.setProfileToken();
        String presetTo = this.ptz.setPreset(presetName, presetToken, this.profileToken);
        return presetTo;
    }

    public boolean removePreset(String presetToken) {
        this.setPtz();
        this.setProfileToken();
        return this.ptz.removePreset(presetToken, this.profileToken);
    }

    public boolean gotoPreset(String presetToken) {
        this.setPtz();
        this.setProfileToken();
        return this.ptz.gotoPreset(presetToken, this.profileToken);
    }

    public boolean stopMove() {
        this.setPtz();
        this.setProfileToken();
        return this.ptz.stopMove(this.profileToken);
    }

    public boolean zoomOut(int speed) {
        this.setPtz();
        this.setProfileToken();
        return this.ptz.continuousMove(this.profileToken, 0.0F, 0.0F, 0.1F * (float)speed);
    }

    public boolean zoomIn(int speed) {
        this.setPtz();
        this.setProfileToken();
        return this.ptz.continuousMove(this.profileToken, 0.0F, 0.0F, -0.1F * (float)speed);
    }

    public boolean continuousMove(int direction, int speed) {
        this.setPtz();
        this.setProfileToken();
        boolean isSuccess = false;
        if (direction == 1) {
            isSuccess = this.ptz.continuousMove(this.profileToken, 0.0F, 0.1F * (float)speed, 0.0F);
        } else if (direction == 2) {
            isSuccess = this.ptz.continuousMove(this.profileToken, 0.0F, -0.1F * (float)speed, 0.0F);
        } else if (direction == 3) {
            isSuccess = this.ptz.continuousMove(this.profileToken, -0.1F * (float)speed, 0.0F, 0.0F);
        } else {
            if (direction != 4) {
                return false;
            }

            isSuccess = this.ptz.continuousMove(this.profileToken, 0.1F * (float)speed, 0.0F, 0.0F);
        }

        return isSuccess;
    }

    public List<RtspUrl> getRtspUrlList() {
        ArrayList<RtspUrl> resultList = new ArrayList();
        List<Profile> profiles = this.nvt.getDevices().getProfiles();
        Iterator i$ = profiles.iterator();

        while(i$.hasNext()) {
            Profile p = (Profile)i$.next();

            try {
                RtspUrl temp = new RtspUrl();
                if ("mainStream".equals(p.getName())) {
                    temp.setType(1);
                } else if ("subStream".equals(p.getName())) {
                    temp.setType(2);
                } else if ("thirdStream".equals(p.getName())) {
                    temp.setType(3);
                }

                String oldUrl = this.nvt.getMedia().getRTSPStreamUri(p.getToken());
                String newUrl = oldUrl.replace("//", "//" + this.nvt.getUsername() + ":" + this.nvt.getPassword() + "@");
                temp.setUrl(newUrl.substring(0, newUrl.indexOf(38)));
                resultList.add(temp);
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        }

        return resultList;
    }

    private void setPtz() {
        if (this.ptz == null) {
            this.ptz = this.nvt.getPtz();
        }

    }

    private void setProfileToken() {
        if (this.profileToken == null) {
            this.profileToken = ((Profile)this.nvt.getDevices().getProfiles().get(0)).getToken();
        }

    }

    private void setOSD() {
        if (this.osd == null) {
            this.osd = this.nvt.getOSDDevices();
        }

    }

    private void setVideoSourceToken() {
        if (this.videoSourceToken == null) {
            this.videoSourceToken = this.osd.getVideoSourceToken(this.osd.getOSDs());
        }

    }
}
