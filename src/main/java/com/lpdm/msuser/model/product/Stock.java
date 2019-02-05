package com.lpdm.msuser.model.product;

import java.time.LocalDate;

public class Stock {

    private Integer id;
    private int quantity;
    private LocalDate expireDate;
    private String packaging;
    private int unitByPackage;
    private int productId;
    private String description;

    public Stock() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public int getUnitByPackage() {
        return unitByPackage;
    }

    public void setUnitByPackage(int unitByPackage) {
        this.unitByPackage = unitByPackage;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", expireDate=" + expireDate +
                ", packaging='" + packaging + '\'' +
                ", unitByPackage=" + unitByPackage +
                ", productId=" + productId +
                ", description='" + description + '\'' +
                '}';
    }
}
