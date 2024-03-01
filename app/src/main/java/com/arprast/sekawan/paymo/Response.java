package com.arprast.sekawan.paymo;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Response<T>  {
    public static final String KEY_TYPE = "type";
    public static final String TYPE_GENERATE_TOKEN = "TYPE_GENERATE_TOKEN";
    private int responseCode;
    private String responseMessage;
    private String responseId;
    private String type;

    private long requestTime;
    private T body;

    public Response() {
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

