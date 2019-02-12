package com.lpdm.msuser.services.shop.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.order.*;
import com.lpdm.msuser.proxy.OrderProxy;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.utils.cookie.CookieUtils;
import com.lpdm.msuser.utils.order.OrderUtils;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    public Order saveOrder(Order order) {

        return orderProxy.saveOrder(order);
    }

    @Override
    public void cancelOrderById(int id) {

        Order order = orderProxy.getOrderById(id);

        order.setStatus(Status.CANCELLED);
        orderProxy.saveOrder(order);
    }

    @Override
    public Order getOrderById(int orderId) {

        return orderProxy.getOrderById(orderId);
    }

    @Override
    public void setOrderToCookie(Order order, HttpServletResponse response) throws JsonProcessingException {

        order.setTotal(OrderUtils.getTotalOrderAmount(order));
        order.setCustomer(new User(order.getCustomer().getId()));

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){
            orderedProduct.getProduct().setListStock(null);
        }

        response.addCookie(CookieUtils.getCookieFromOrder(order));
    }

    @Override
    public List<Delivery> findAllDeliveryMethods() {

        return orderProxy.findAllDeliveryMethods();
    }

    @Override
    public List<Payment> findAllPayments() {

        return orderProxy.findAllPaymentMethods();
    }

    @Override
    public Order findLastOrderByCustomerAndStatus(int customer, int status) {

        Order order = null;

        try{ order = orderProxy.findLastOrderByCustomerAndStatus(customer, status); }
        catch (FeignException e){ log.warn(e.getMessage()); }

        return order;
    }

    @Override
    public PaypalUrl getPaypalPaymentUrl(int order, SuccessUrl urls) {

        return orderProxy.getPayPalUrl(order, urls);
    }

}
