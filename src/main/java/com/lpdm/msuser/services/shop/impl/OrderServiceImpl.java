package com.lpdm.msuser.services.shop.impl;

import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.services.shop.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public int getTotalOrderedProducts(Order order) {

        return order.getOrderedProducts().stream().mapToInt(OrderedProduct::getQuantity).sum();
    }

    @Override
    public double getOrderedProductTotalAmount(OrderedProduct orderedProduct) {

        double totalAmount = 0;

        double totalWithoutTax = orderedProduct.getQuantity() * orderedProduct.getProduct().getPrice();
        double taxAmount = totalWithoutTax * (orderedProduct.getProduct().getTax() / 100);

        totalAmount = totalWithoutTax + taxAmount;

        return Math.round(totalAmount * 100D) / 100D;
    }

    @Override
    public double getOrderedProductPriceWithTax(OrderedProduct orderedProduct) {

        double taxAmount = orderedProduct.getProduct().getPrice() * (orderedProduct.getProduct().getTax() / 100);

        log.info("Tax amount = " + taxAmount);

        double priceWithTax = orderedProduct.getProduct().getPrice() + taxAmount;

        return Math.round(priceWithTax * 100D) / 100D;
    }

    @Override
    public double getTotalOrderAmount(Order order) {

        double totalAmount = 0;

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){

            double amount = getOrderedProductTotalAmount(orderedProduct);

            totalAmount += amount;
        }

        log.info("Order total amount : " + totalAmount);

        return Math.round(totalAmount * 100D) / 100D;
    }

}
