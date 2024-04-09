package com.yyzy.pz.onvif.soap.devices;

import com.yyzy.pz.onvif.soap.OnvifDevice;
import com.yyzy.pz.onvif.soap.SOAP;
import org.onvif.ver10.media.wsdl.*;
import org.onvif.ver10.schema.*;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.util.List;

public class MediaDevices {
    private OnvifDevice onvifDevice;
    private SOAP soap;

    public MediaDevices(OnvifDevice onvifDevice) {
        this.onvifDevice = onvifDevice;
        this.soap = onvifDevice.getSoap();
    }

    /** @deprecated */
    @Deprecated
    public String getHTTPStreamUri(int profileNumber) throws ConnectException, SOAPException {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.HTTP);
        setup.setTransport(transport);
        return this.getStreamUri(setup, profileNumber);
    }

    public String getHTTPStreamUri(String profileToken) throws ConnectException, SOAPException {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.HTTP);
        setup.setTransport(transport);
        return this.getStreamUri(profileToken, setup);
    }

    /** @deprecated */
    @Deprecated
    public String getUDPStreamUri(int profileNumber) throws ConnectException, SOAPException {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.UDP);
        setup.setTransport(transport);
        return this.getStreamUri(setup, profileNumber);
    }

    public String getUDPStreamUri(String profileToken) throws ConnectException, SOAPException {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.UDP);
        setup.setTransport(transport);
        return this.getStreamUri(profileToken, setup);
    }

    /** @deprecated */
    @Deprecated
    public String getTCPStreamUri(int profileNumber) throws ConnectException, SOAPException {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.TCP);
        setup.setTransport(transport);
        return this.getStreamUri(setup, profileNumber);
    }

    public String getTCPStreamUri(String profileToken) throws ConnectException, SOAPException {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.TCP);
        setup.setTransport(transport);
        return this.getStreamUri(profileToken, setup);
    }

    /** @deprecated */
    @Deprecated
    public String getRTSPStreamUri(int profileNumber) throws ConnectException, SOAPException {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.TCP);
        setup.setTransport(transport);
        return this.getStreamUri(setup, profileNumber);
    }

    public String getRTSPStreamUri(String profileToken) throws ConnectException, SOAPException {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.TCP);
        setup.setTransport(transport);
        return this.getStreamUri(profileToken, setup);
    }

    /** @deprecated */
    @Deprecated
    public String getStreamUri(StreamSetup streamSetup, int profileNumber) throws ConnectException, SOAPException {
        Profile profile = (Profile)this.onvifDevice.getDevices().getProfiles().get(profileNumber);
        return this.getStreamUri(profile, streamSetup);
    }

    /** @deprecated */
    @Deprecated
    public String getStreamUri(Profile profile, StreamSetup streamSetup) throws ConnectException, SOAPException {
        return this.getStreamUri(profile.getToken(), streamSetup);
    }

    public String getStreamUri(String profileToken, StreamSetup streamSetup) throws SOAPException, ConnectException {
        GetStreamUri request = new GetStreamUri();
        GetStreamUriResponse response = new GetStreamUriResponse();
        request.setProfileToken(profileToken);
        request.setStreamSetup(streamSetup);

        try {
            response = (GetStreamUriResponse)this.soap.createSOAPMediaRequest(request, response, false);
        } catch (ConnectException | SOAPException var6) {
            throw var6;
        }

        return response == null ? null : this.onvifDevice.replaceLocalIpWithProxyIp(response.getMediaUri().getUri());
    }

    public static VideoEncoderConfiguration getVideoEncoderConfiguration(Profile profile) {
        return profile.getVideoEncoderConfiguration();
    }

    public VideoEncoderConfigurationOptions getVideoEncoderConfigurationOptions(String profileToken) throws SOAPException, ConnectException {
        GetVideoEncoderConfigurationOptions request = new GetVideoEncoderConfigurationOptions();
        GetVideoEncoderConfigurationOptionsResponse response = new GetVideoEncoderConfigurationOptionsResponse();
        request.setProfileToken(profileToken);

        try {
            response = (GetVideoEncoderConfigurationOptionsResponse)this.soap.createSOAPMediaRequest(request, response, false);
        } catch (ConnectException | SOAPException var5) {
            throw var5;
        }

        return response == null ? null : response.getOptions();
    }

    public boolean setVideoEncoderConfiguration(VideoEncoderConfiguration videoEncoderConfiguration) throws SOAPException, ConnectException {
        SetVideoEncoderConfiguration request = new SetVideoEncoderConfiguration();
        SetVideoEncoderConfigurationResponse response = new SetVideoEncoderConfigurationResponse();
        request.setConfiguration(videoEncoderConfiguration);
        request.setForcePersistence(true);

        try {
            response = (SetVideoEncoderConfigurationResponse)this.soap.createSOAPMediaRequest(request, response, true);
        } catch (ConnectException | SOAPException var5) {
            throw var5;
        }

        return response != null;
    }

    public String getSceenshotUri(String profileToken) throws SOAPException, ConnectException {
        return this.getSnapshotUri(profileToken);
    }

    public String getSnapshotUri(String profileToken) throws SOAPException, ConnectException {
        GetSnapshotUri request = new GetSnapshotUri();
        GetSnapshotUriResponse response = new GetSnapshotUriResponse();
        request.setProfileToken(profileToken);

        try {
            response = (GetSnapshotUriResponse)this.soap.createSOAPMediaRequest(request, response, true);
        } catch (ConnectException | SOAPException var5) {
            throw var5;
        }

        return response != null && response.getMediaUri() != null ? this.onvifDevice.replaceLocalIpWithProxyIp(response.getMediaUri().getUri()) : null;
    }

    public List<VideoSource> getVideoSources() throws SOAPException, ConnectException {
        GetVideoSources request = new GetVideoSources();
        GetVideoSourcesResponse response = new GetVideoSourcesResponse();

        try {
            response = (GetVideoSourcesResponse)this.soap.createSOAPMediaRequest(request, response, false);
        } catch (ConnectException | SOAPException var4) {
            throw var4;
        }

        return response == null ? null : response.getVideoSources();
    }
}
