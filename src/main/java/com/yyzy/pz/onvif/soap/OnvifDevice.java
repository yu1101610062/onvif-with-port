package com.yyzy.pz.onvif.soap;

import com.yyzy.pz.onvif.log.Logger;
import com.yyzy.pz.onvif.soap.devices.*;
import org.onvif.ver10.schema.Capabilities;

import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnvifDevice {
    private String HOST_IP;
    private int HOST_PORT;
    private String originalIp;
    private boolean isProxy;
    private String username;
    private String password;
    private String nonce;
    private String utcTime;
    private String serverDeviceUri;
    private String serverPtzUri;
    private String serverMediaUri;
    private String serverImagingUri;
    private String serverEventsUri;
    private SOAP soap;
    private InitialDevices initialDevices;
    private PtzDevices ptzDevices;
    private OSDDevices osdDevices;
    private MediaDevices mediaDevices;
    private ImagingDevices imagingDevices;
    private Logger logger;

    public OnvifDevice(String hostIp, int port, String userName, String password) throws ConnectException, SOAPException {
        this.logger = new Logger();
        this.HOST_IP = hostIp;
        this.HOST_PORT = port;
        if (!this.isOnline()) {
            throw new ConnectException("Host not available.");
        } else {
            this.serverDeviceUri = "http://" + this.HOST_IP + ":" + this.HOST_PORT + "/onvif/device_service";
            this.username = userName;
            this.password = password;
            this.soap = new SOAP(this);
            this.initialDevices = new InitialDevices(this);
            this.ptzDevices = new PtzDevices(this);
            this.osdDevices = new OSDDevices(this);
            this.mediaDevices = new MediaDevices(this);
            this.imagingDevices = new ImagingDevices(this);
            this.init();
        }
    }

    public OnvifDevice(String hostIp) throws ConnectException, SOAPException {
        this(hostIp, 80, ( String ) null, ( String ) null);
    }

    private boolean isOnline() {
        String port = this.HOST_IP.contains(":") ? this.HOST_IP.substring(this.HOST_IP.indexOf(58) + 1) : "80";
        String ip = this.HOST_IP.contains(":") ? this.HOST_IP.substring(0, this.HOST_IP.indexOf(58)) : this.HOST_IP;
        Socket socket = null;

        boolean var5;
        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, Integer.parseInt(port));
            socket = new Socket();
            socket.connect(sockaddr, 2000);
            return true;
        } catch (IOException | NumberFormatException var15) {
            var5 = false;
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException var14) {
            }

        }

        return var5;
    }

    protected void init() throws ConnectException, SOAPException {
        Capabilities capabilities = this.getDevices().getCapabilities();
        if (capabilities == null) {
            throw new ConnectException("Capabilities not reachable.");
        } else {
            String localDeviceUri = capabilities.getDevice().getXAddr();
            if (localDeviceUri.startsWith("http://")) {
                this.originalIp = localDeviceUri.replace("http://", "");
                this.originalIp = this.originalIp.substring(0, this.originalIp.indexOf(47));
            } else {
                this.logger.error("Unknown/Not implemented local procotol!");
            }

            if (!this.originalIp.equals(this.HOST_IP)) {
                this.isProxy = true;
            }

            if (capabilities.getMedia() != null && capabilities.getMedia().getXAddr() != null) {
                this.serverMediaUri = this.replaceLocalIpWithProxyIp(capabilities.getMedia().getXAddr());
            }

            if (capabilities.getPTZ() != null && capabilities.getPTZ().getXAddr() != null) {
                this.serverPtzUri = this.replaceLocalIpWithProxyIp(capabilities.getPTZ().getXAddr());
            }

            if (capabilities.getImaging() != null && capabilities.getImaging().getXAddr() != null) {
                this.serverImagingUri = this.replaceLocalIpWithProxyIp(capabilities.getImaging().getXAddr());
            }

            if (capabilities.getEvents() != null && capabilities.getEvents().getXAddr() != null) {
                this.serverEventsUri = this.replaceLocalIpWithProxyIp(capabilities.getEvents().getXAddr());
            }

        }
    }

    public String replaceLocalIpWithProxyIp(String original) {
        if (original.startsWith("http:///")) {
            original.replace("http:///", "http://" + this.HOST_IP);
        }
        String url = this.isProxy ? original.replace(this.originalIp, this.HOST_IP) : original;
        //找到url中的端口，替换为81
        return changePortInUrl(url, this.HOST_PORT);
    }

    /**
     * 替换或添加URL中的端口
     *
     * @param url     原始的URL字符串
     * @param newPort 新的端口字符串
     * @return 修改端口后的URL字符串
     */
    public static String changePortInUrl(String url, Integer newPort) {
        if (newPort == null) {
            return url;
        }
        // 正则表达式1，匹配包含端口号的URL
        String regexWithPort = "^(https?://[^/:]+):[^/]*(.*)";
        // 正则表达式2，匹配不包含端口号的URL
        String regexWithoutPort = "^(https?://[^/:]+)(.*)";

        // 尝试匹配包含端口号的URL
        Pattern patternWithPort = Pattern.compile(regexWithPort);
        Matcher matcherWithPort = patternWithPort.matcher(url);
        if (matcherWithPort.find()) {
            // 如果找到了则替换端口
            return matcherWithPort.replaceAll("$1:" + newPort + "$2");
        } else {
            // 如果没有找到，匹配不包含端口号的URL
            Pattern patternWithoutPort = Pattern.compile(regexWithoutPort);
            Matcher matcherWithoutPort = patternWithoutPort.matcher(url);
            if (matcherWithoutPort.find()) {
                // 如果找到了，则在URL中添加新的端口号
                return matcherWithoutPort.replaceAll("$1:" + newPort + "$2");
            }
        }
        // 如果两种模式都没有匹配，直接返回原URL
        return url;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEncryptedPassword() {
        return this.encryptPassword();
    }

    public String encryptPassword() {
        String nonce = this.getNonce();
        String timestamp = this.getUTCTime();
        String beforeEncryption = nonce + timestamp + this.password;

        byte[] encryptedRaw;
        try {
            encryptedRaw = sha1(beforeEncryption);
        } catch (NoSuchAlgorithmException var6) {
            var6.printStackTrace();
            return null;
        }

        return Base64.getEncoder().encodeToString(encryptedRaw);
    }

    private static byte[] sha1(String s) throws NoSuchAlgorithmException {
        MessageDigest SHA1 = null;
        SHA1 = MessageDigest.getInstance("SHA1");
        SHA1.reset();
        SHA1.update(s.getBytes());
        byte[] encryptedRaw = SHA1.digest();
        return encryptedRaw;
    }

    private String getNonce() {
        if (this.nonce == null) {
            this.createNonce();
        }

        return this.nonce;
    }

    public String getEncryptedNonce() {
        if (this.nonce == null) {
            this.createNonce();
        }

        return Base64.getEncoder().encodeToString(this.nonce.getBytes());
    }

    public void createNonce() {
        Random generator = new Random();
        this.nonce = "" + generator.nextInt();
    }

    public String getLastUTCTime() {
        return this.utcTime;
    }

    public String getUTCTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");
        sdf.setTimeZone(new SimpleTimeZone(2, "UTC"));
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        String utcTime = sdf.format(cal.getTime());
        this.utcTime = utcTime;
        return utcTime;
    }

    public SOAP getSoap() {
        return this.soap;
    }

    public InitialDevices getDevices() {
        return this.initialDevices;
    }

    public PtzDevices getPtz() {
        return this.ptzDevices;
    }

    public OSDDevices getOSDDevices() {
        return this.osdDevices;
    }

    public MediaDevices getMedia() {
        return this.mediaDevices;
    }

    public ImagingDevices getImaging() {
        return this.imagingDevices;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public String getDeviceUri() {
        return this.serverDeviceUri;
    }

    protected String getPtzUri() {
        return this.serverPtzUri;
    }

    protected String getMediaUri() {
        return this.serverMediaUri;
    }

    protected String getImagingUri() {
        return this.serverImagingUri;
    }

    protected String getEventsUri() {
        return this.serverEventsUri;
    }

    public Date getDate() {
        return this.initialDevices.getDate();
    }

    public String getName() {
        return this.initialDevices.getDeviceInformation().getModel();
    }

    public String getHostname() {
        return this.initialDevices.getHostname();
    }

    public String reboot() throws ConnectException, SOAPException {
        return this.initialDevices.reboot();
    }

    public String getPassword() {
        return this.password;
    }
}

