package com.lpdm.msuser.model.order;

import com.lpdm.msuser.model.product.Product;


public class OrderedProduct {

    private int id;
    private Order order;
    private int productId;
    private Product product;
    private int quantity;
    private double price;
    private double tax;
    private double totalAmount;
    private double priceWithTax;

    public OrderedProduct() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPriceWithTax() {
        return priceWithTax;
    }

    public void setPriceWithTax(double priceWithTax) {
        this.priceWithTax = priceWithTax;
    }

    @Override
    public String toString() {
        return "OrderedProduct{" +
                "id=" + id +
                ", order=" + order +
                ", productId=" + productId +
                ", product=" + product +
                ", quantity=" + quantity +
                ", price=" + price +
                ", tax=" + tax +
                ", totalAmount=" + totalAmount +
                ", priceWithTax=" + priceWithTax +
                '}';
    }
}
