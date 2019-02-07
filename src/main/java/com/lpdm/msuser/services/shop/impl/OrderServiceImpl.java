package com.lpdm.msuser.services.shop.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.proxy.OrderProxy;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.utils.cookie.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class OrderServiceImpl implements OrderService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final OrderProxy orderProxy;

    @Autowired
    public OrderServiceImpl(OrderProxy orderProxy) {
        this.orderProxy = orderProxy;
    }

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

    @Override
    public Order saveOrder(Order order) {

        return orderProxy.saveOrder(order);
    }

    @Override
    public Order getOrderById(int orderId) {

        return orderProxy.getOrderById(orderId);
    }

    @Override
    public void setOrderToCookie(Order order, HttpServletResponse response) throws JsonProcessingException {

        order.setTotal(getTotalOrderAmount(order));
        order.setCustomer(new User(order.getCustomer().getId()));

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){
            orderedProduct.getProduct().setListStock(null);
        }

        response.addCookie(CookieUtils.getCookieFromOrder(order));
    }

}
