package com.yyzy.pz.onvif.soap.devices;


import com.yyzy.pz.onvif.soap.OnvifDevice;
import com.yyzy.pz.onvif.soap.SOAP;
import org.onvif.ver10.device.wsdl.*;
import org.onvif.ver10.media.wsdl.*;
import org.onvif.ver10.schema.*;
import org.onvif.ver10.schema.Capabilities;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class InitialDevices {
    private SOAP soap;
    private OnvifDevice onvifDevice;

    public InitialDevices(OnvifDevice onvifDevice) {
        this.onvifDevice = onvifDevice;
        this.soap = onvifDevice.getSoap();
    }

    public Date getDate() {
        Calendar cal = null;
        GetSystemDateAndTimeResponse response = new GetSystemDateAndTimeResponse();

        try {
            response = (GetSystemDateAndTimeResponse)this.soap.createSOAPDeviceRequest(new GetSystemDateAndTime(), response, false);
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return null;
        }

        org.onvif.ver10.schema.Date date = response.getSystemDateAndTime().getUTCDateTime().getDate();
        Time time = response.getSystemDateAndTime().getUTCDateTime().getTime();
        cal = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay(), time.getHour(), time.getMinute(), time.getSecond());
        return cal.getTime();
    }

    public GetDeviceInformationResponse getDeviceInformation() {
        GetDeviceInformation getHostname = new GetDeviceInformation();
        GetDeviceInformationResponse response = new GetDeviceInformationResponse();

        try {
            response = (GetDeviceInformationResponse)this.soap.createSOAPDeviceRequest(getHostname, response, true);
            return response;
        } catch (ConnectException | SOAPException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public String getHostname() {
        GetHostname getHostname = new GetHostname();
        GetHostnameResponse response = new GetHostnameResponse();

        try {
            response = (GetHostnameResponse)this.soap.createSOAPDeviceRequest(getHostname, response, true);
        } catch (ConnectException | SOAPException var4) {
            var4.printStackTrace();
            return null;
        }

        return response.getHostnameInformation().getName();
    }

    public boolean setHostname(String hostname) {
        SetHostname setHostname = new SetHostname();
        setHostname.setName(hostname);
        SetHostnameResponse response = new SetHostnameResponse();

        try {
            response = (SetHostnameResponse)this.soap.createSOAPDeviceRequest(setHostname, response, true);
            return true;
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return false;
        }
    }

    public List<User> getUsers() {
        GetUsers getUsers = new GetUsers();
        GetUsersResponse response = new GetUsersResponse();

        try {
            response = (GetUsersResponse)this.soap.createSOAPDeviceRequest(getUsers, response, true);
        } catch (ConnectException | SOAPException var4) {
            var4.printStackTrace();
            return null;
        }

        return response == null ? null : response.getUser();
    }

    public Capabilities getCapabilities() throws ConnectException, SOAPException {
        GetCapabilities getCapabilities = new GetCapabilities();
        GetCapabilitiesResponse response = new GetCapabilitiesResponse();

        try {
            response = (GetCapabilitiesResponse)this.soap.createSOAPRequest(getCapabilities, response, this.onvifDevice.getDeviceUri(), false);
        } catch (SOAPException var4) {
            throw var4;
        }

        return response == null ? null : response.getCapabilities();
    }

    public List<Profile> getProfiles() {
        GetProfiles request = new GetProfiles();
        GetProfilesResponse response = new GetProfilesResponse();

        try {
            response = (GetProfilesResponse)this.soap.createSOAPMediaRequest(request, response, true);
        } catch (ConnectException | SOAPException var4) {
            var4.printStackTrace();
            return null;
        }

        return response == null ? null : response.getProfiles();
    }

    public Profile getProfile(String profileToken) {
        GetProfile request = new GetProfile();
        GetProfileResponse response = new GetProfileResponse();
        request.setProfileToken(profileToken);

        try {
            response = (GetProfileResponse)this.soap.createSOAPMediaRequest(request, response, true);
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return null;
        }

        return response == null ? null : response.getProfile();
    }

    public Profile createProfile(String name) {
        CreateProfile request = new CreateProfile();
        CreateProfileResponse response = new CreateProfileResponse();
        request.setName(name);

        try {
            response = (CreateProfileResponse)this.soap.createSOAPMediaRequest(request, response, true);
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return null;
        }

        return response == null ? null : response.getProfile();
    }

    public List<Service> getServices(boolean includeCapability) {
        GetServices request = new GetServices();
        GetServicesResponse response = new GetServicesResponse();
        request.setIncludeCapability(includeCapability);

        try {
            response = (GetServicesResponse)this.soap.createSOAPDeviceRequest(request, response, true);
        } catch (ConnectException | SOAPException var5) {
            var5.printStackTrace();
            return null;
        }

        return response == null ? null : response.getService();
    }

    public List<Scope> getScopes() {
        GetScopes request = new GetScopes();
        GetScopesResponse response = new GetScopesResponse();

        try {
            response = (GetScopesResponse)this.soap.createSOAPMediaRequest(request, response, true);
        } catch (ConnectException | SOAPException var4) {
            var4.printStackTrace();
            return null;
        }

        return response == null ? null : response.getScopes();
    }

    public String reboot() throws ConnectException, SOAPException {
        SystemReboot request = new SystemReboot();
        SystemRebootResponse response = new SystemRebootResponse();

        try {
            response = (SystemRebootResponse)this.soap.createSOAPMediaRequest(request, response, true);
        } catch (ConnectException | SOAPException var4) {
            throw var4;
        }

        return response == null ? null : response.getMessage();
    }
}
