package com.lpdm.msuser.services.shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.order.*;
import com.lpdm.msuser.model.paypal.TransactionInfo;
import com.lpdm.msuser.model.shop.Cart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface OrderService {

    int getTotalOrderedProducts(Order order);

    Order saveOrder(Order order);

    void cancelOrderById(int id);

    Order getOrderById(int orderId);

    Order getOrderFromCookie(HttpServletRequest request);

    int getOrderIdFromCookie(HttpServletRequest request);

    void setOrderToCookie(Order order, HttpServletResponse response) throws JsonProcessingException;

    List<Delivery> findAllDeliveryMethods();

    List<Payment> findAllPayments();

    Order findLastOrderByCustomerAndStatus(int customer, int status);

    PaypalUrl getPaypalPaymentUrl(int order, SuccessUrl urls);

    String getTransactionDetails(TransactionInfo transactionInfo);

    List<Order> findAllByCustomerSorted(int customer, String sort);

    List<Order> findAllByCustomerAndStatus(int customer, int status);
}
