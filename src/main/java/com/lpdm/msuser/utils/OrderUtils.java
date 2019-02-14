package com.lpdm.msuser.utils;

import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import org.springframework.stereotype.Component;

@Component
public class OrderUtils {

    public static double getOrderedProductPriceWithTax(OrderedProduct orderedProduct) {

        double taxAmount = orderedProduct.getProduct().getPrice() * (orderedProduct.getProduct().getTax() / 100);

        double priceWithTax = orderedProduct.getProduct().getPrice() + taxAmount;

        return Math.round(priceWithTax * 100D) / 100D;
    }

    public static double getOrderedProductTotalAmount(OrderedProduct orderedProduct) {

        double totalAmount = 0;

        double totalWithoutTax = orderedProduct.getQuantity() * orderedProduct.getProduct().getPrice();
        double taxAmount = totalWithoutTax * (orderedProduct.getProduct().getTax() / 100);

        totalAmount = totalWithoutTax + taxAmount;

        return Math.round(totalAmount * 100D) / 100D;
    }

    public static double getTotalOrderAmount(Order order) {

        double totalAmount = 0;

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){

            double amount = getOrderedProductTotalAmount(orderedProduct);

            totalAmount += amount;
        }

        return Math.round(totalAmount * 100D) / 100D;
    }

    public static double getTotalOrderAmountWithoutTax(Order order) {

        double totalAmount = 0;

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){

            totalAmount += orderedProduct.getQuantity() * orderedProduct.getProduct().getPrice();

        }
        return Math.round(totalAmount * 100D) / 100D;
    }

    public static Order setAllAmountsFromOrder(Order order){

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){

            orderedProduct.setTotalAmount(OrderUtils.getOrderedProductTotalAmount(orderedProduct));
            orderedProduct.setPriceWithTax(OrderUtils.getOrderedProductPriceWithTax(orderedProduct));
        }

        order.setTotal(getTotalOrderAmount(order));

        return order;
    }

    public static int getTotalOrderedProducts(Order order){

        return order.getOrderedProducts().stream().mapToInt(OrderedProduct::getQuantity).sum();
    }
}
