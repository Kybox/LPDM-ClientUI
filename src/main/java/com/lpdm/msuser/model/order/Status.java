package com.lpdm.msuser.model.order;

public enum Status {

    CART(1), VALIDATED(2) , PAID(3), PROCESSED(4), IN_DELIVERING(5), DELIVERED(6);

    private int id;

    Status(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

