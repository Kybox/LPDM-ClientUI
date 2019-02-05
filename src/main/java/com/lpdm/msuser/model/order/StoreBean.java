package com.lpdm.msuser.model.order;

public class StoreBean {

    private int id;
    private String name;
    private LocationBean location;

    public StoreBean() {
    }

    public StoreBean(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }
}
