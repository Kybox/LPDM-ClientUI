package com.lpdm.msuser.model.order;

public class Coupon {

    private int id;
    private boolean active;
    private double amount;
    private String code;
    private String description;

    public Coupon() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", active=" + active +
                ", amount=" + amount +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
