package com.lpdm.msuser.model.order;

public class Payment {

    private int id;
    private String label;

    public Payment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}
