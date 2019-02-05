package com.lpdm.msuser.model.order;

public class Delivery {

    private int id;
    private String method;
    private double amount;

    public Delivery() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", amount=" + amount +
                '}';
    }
}
