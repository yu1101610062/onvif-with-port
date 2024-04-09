package com.yyzy.pz;

public class RtspUrl {
    public static final int RTSP_TYPE_MAIN_STREAM = 1;
    public static final int RTSP_TYPE_SUB_STREAM = 2;
    public static final int RTSP_TYPE_THIRD_STREAM = 3;
    private int type;
    private String url;

    public RtspUrl() {
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
