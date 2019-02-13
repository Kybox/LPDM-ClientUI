package com.lpdm.msuser.model.shop;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private int id;
    private List<CookieProduct> productList;
    private int totalProducts;
    private double amountWithTax;
    private double amountWithoutTax;

    public Cart() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<CookieProduct> getProductList() {
        if(productList == null) productList = new ArrayList<>();
        return productList;
    }

    public void setProductList(List<CookieProduct> productList) {
        this.productList = productList;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public double getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(double amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public double getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(double amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", productList=" + productList +
                ", totalProducts=" + totalProducts +
                ", amountWithTax=" + amountWithTax +
                ", amountWithoutTax=" + amountWithoutTax +
                '}';
    }
}
