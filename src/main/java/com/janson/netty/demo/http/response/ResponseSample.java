package com.janson.netty.demo.http.response;

/**
 * @Description:
 * @Author: Janson
 * @Date: 2021/8/21 18:40
 **/
public class ResponseSample {
    private String code;
    private String data;
    private long timeSample;

    public ResponseSample() {
    }

    public ResponseSample(String code, String data, long timeSample) {
        this.code = code;
        this.data = data;
        this.timeSample = timeSample;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimeSample() {
        return timeSample;
    }

    public void setTimeSample(long timeSample) {
        this.timeSample = timeSample;
    }
}
