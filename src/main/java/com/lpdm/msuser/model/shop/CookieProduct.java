package com.lpdm.msuser.model.shop;

public class CookieProduct {

    private int id;
    private String name;
    private int quantity;
    private String picture;
    private double price;
    private double tax;
    private double priceWithTax;

    public CookieProduct() {
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getPriceWithTax() {
        return priceWithTax;
    }

    public void setPriceWithTax(double priceWithTax) {
        this.priceWithTax = priceWithTax;
    }

    @Override
    public String toString() {
        return "CookieProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", picture='" + picture + '\'' +
                ", price=" + price +
                ", tax=" + tax +
                ", priceWithTax=" + priceWithTax +
                '}';
    }
}
