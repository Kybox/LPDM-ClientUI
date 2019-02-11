package com.lpdm.msuser.services.shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.order.Delivery;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.model.order.Payment;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface OrderService {

    int getTotalOrderedProducts(Order order);

    double getOrderedProductTotalAmount(OrderedProduct orderedProduct);

    double getOrderedProductPriceWithTax(OrderedProduct orderedProduct);

    double getTotalOrderAmount(Order order);

    double getTotalOrderAmountWithoutTax(Order order);

    Order saveOrder(Order order);

    Order getOrderById(int orderId);

    void setOrderToCookie(Order order, HttpServletResponse response) throws JsonProcessingException;

    List<Delivery> findAllDeliveryMethods();

    List<Payment> findAllPayments();
}
