package com.lpdm.msuser.model.order;

import java.util.Map;

public class PaypalUrl {

    private String redirectUrl;

    private Map<String, String > headers;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
