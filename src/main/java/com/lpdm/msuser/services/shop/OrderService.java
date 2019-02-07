package com.lpdm.msuser.services.shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;

import javax.servlet.http.HttpServletResponse;

public interface OrderService {

    int getTotalOrderedProducts(Order order);

    double getOrderedProductTotalAmount(OrderedProduct orderedProduct);

    double getOrderedProductPriceWithTax(OrderedProduct orderedProduct);

    double getTotalOrderAmount(Order order);

    Order saveOrder(Order order);

    Order getOrderById(int orderId);

    void setOrderToCookie(Order order, HttpServletResponse response) throws JsonProcessingException;
}
