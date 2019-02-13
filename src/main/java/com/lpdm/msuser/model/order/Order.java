package com.lpdm.msuser.model.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.utils.json.ParseDeserializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private int id;
    private double total;

    @JsonDeserialize(using = ParseDeserializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime orderDate;

    private Status status;
    private Payment payment;
    private int storeId;
    private Store store;
    private int customerId;
    private User customer;
    private Coupon coupon;
    private Delivery delivery;
    private double taxAmount;
    private List<OrderedProduct> orderedProducts;

    // ClientUI
    private int totalProducts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public List<OrderedProduct> getOrderedProducts() {

        if(orderedProducts == null) orderedProducts = new ArrayList<>();
        return orderedProducts;
    }

    public void setOrderedProducts(List<OrderedProduct> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", total=" + total +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", payment=" + payment +
                ", storeId=" + storeId +
                ", store=" + store +
                ", customerId=" + customerId +
                ", customer=" + customer +
                ", coupon=" + coupon +
                ", delivery=" + delivery +
                ", taxAmount=" + taxAmount +
                ", orderedProducts=" + orderedProducts +
                '}';
    }
}
