package com.arprast.sekawan.paymo;

public class Request<T> {
    public static final String TYPE_GENERATE_TOKEN = "TYPE_GENERATE_TOKEN";
    private String requestId;
    private String type;
    private long requestTime;
    private T body;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
