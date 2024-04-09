package com.yyzy;

import com.yyzy.pz.OnvifAPI;

import java.net.ConnectException;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

import javax.xml.soap.SOAPException;

public class Main {

    public Result ptzControl(String ip, String userName, String pwd, Integer direction, Integer speed, String op,Integer port) {
        Result result = new Result("控制失败");
        if (Objects.isNull(port)){
            port = 80;
        }

        //添加预置位信息
        try {
            OnvifAPI onvifAPI = new OnvifAPI(ip, port, userName, pwd);
            if ("move".equals(op)) {
                onvifAPI.continuousMove(direction, speed);
            } else {
                onvifAPI.stopMove();
            }
            result.setSuccessResult();
        } catch (ConnectException e) {
            result.setMessage("连接设备超时");
            e.printStackTrace();
        } catch (SOAPException e) {
            result.setMessage("控制设备失败");
            e.printStackTrace();
        }


        return result;
    }


    public Result zoomControl(String ip, String userName, String pwd, Integer speed, String op,Integer port) {
        Result result = new Result("控制失败");
        if(Objects.isNull(port)){
            port = 80;
        }
        try {
            OnvifAPI onvifAPI = new OnvifAPI(ip, port, userName, pwd);
            if ("zoomIn".equals(op)) {
                onvifAPI.zoomIn(speed);
            } else if ("zoomOut".equals(op)) {
                onvifAPI.zoomOut(speed);
            }
            //停止
            else {
                onvifAPI.stopMove();
            }
            result.setSuccessResult();
        } catch (ConnectException e) {
            result.setMessage("连接设备超时");
            e.printStackTrace();
        } catch (SOAPException e) {
            result.setMessage("控制设备失败");
            e.printStackTrace();
        }

        return result;
    }



    public Result delIPCPreset(String ip, String userName, String pwd, String presetToken) {
        Result result = new Result("控制失败");
        try {
            OnvifAPI onvifAPI = new OnvifAPI(ip, 80, userName, pwd);
            onvifAPI.removePreset(presetToken);
            result.setSuccessResult();
        } catch (ConnectException e) {
            result.setMessage("连接设备超时");
            e.printStackTrace();
        } catch (SOAPException e) {
            result.setMessage("控制设备失败");
            e.printStackTrace();
        }

        return result;
    }


    public Result addOrUpdateCameraPreset(String presetToken, String ip, String userName, String pwd, String presetName) {
        Result result = new Result("控制失败");
        try {
            OnvifAPI onvifAPI = new OnvifAPI(ip, 80, userName, pwd);
            if (StringUtils.isNotEmpty(presetToken)) {
                onvifAPI.updatePreset(presetName, presetToken);
                result.setSuccessResult();
            } else {
                result.setSuccessResult(onvifAPI.createPreset(presetName));
            }

            result.setSuccessResult();
        } catch (ConnectException e) {
            result.setMessage("连接设备超时");
            e.printStackTrace();
        } catch (SOAPException e) {
            result.setMessage("控制设备失败");
            e.printStackTrace();
        }

        return result;
    }

    public Result gotoPreset(String presetToken, String ip, String userName, String pwd) {
        Result result = new Result("控制失败");
        try {
            OnvifAPI onvifAPI = new OnvifAPI(ip, 80, userName, pwd);
            onvifAPI.gotoPreset(presetToken);
            result.setSuccessResult();
        } catch (ConnectException e) {
            result.setMessage("连接设备超时");
            e.printStackTrace();
        } catch (SOAPException e) {
            result.setMessage("控制设备失败");
            e.printStackTrace();
        }

        return result;
    }


    public Result stopMove(String ip, String userName, String pwd,Integer port) {
        Result result = new Result("控制失败");
        if(Objects.isNull(port)){
            port = 80;
        }
        try {
            OnvifAPI onvifAPI = new OnvifAPI(ip, port, userName, pwd);
            onvifAPI.stopMove();
            result.setSuccessResult();
        } catch (ConnectException e) {
            result.setMessage("连接设备超时");
            e.printStackTrace();
        } catch (SOAPException e) {
            result.setMessage("控制设备失败");
            e.printStackTrace();
        }

        return result;
    }


    public Result getRtspUrlList(String ip, int port, String userName, String pwd) {
        Result result = new Result("获取设备RTSP地址失败");
        try {
            OnvifAPI onvifAPI = new OnvifAPI(ip, port, userName, pwd);
            result.setSuccessResult(onvifAPI.getRtspUrlList());
        } catch (ConnectException e) {
            result.setMessage("连接设备超时");
            e.printStackTrace();
        } catch (SOAPException e) {
            result.setMessage("获取设备RTSP地址失败");
            e.printStackTrace();
        }

        return result;
    }


    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}