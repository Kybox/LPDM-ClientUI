package com.lpdm.msuser.model.paypal;

public class TransactionInfo {

    private String paymentId;
    private String token;
    private String PayerID;

    public TransactionInfo() {
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPayerID() {
        return PayerID;
    }

    public void setPayerID(String payerID) {
        PayerID = payerID;
    }

    @Override
    public String toString() {
        return "TransactionInfo{" +
                "paymentId='" + paymentId + '\'' +
                ", token='" + token + '\'' +
                ", PayerID='" + PayerID + '\'' +
                '}';
    }
}
