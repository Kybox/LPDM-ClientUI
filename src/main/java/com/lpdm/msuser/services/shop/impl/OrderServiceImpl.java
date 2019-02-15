package com.lpdm.msuser.services.shop.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.order.*;
import com.lpdm.msuser.model.paypal.TransactionInfo;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.model.shop.Cart;
import com.lpdm.msuser.model.shop.CookieProduct;
import com.lpdm.msuser.proxy.OrderProxy;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.services.shop.ProductService;
import com.lpdm.msuser.utils.CartUtils;
import com.lpdm.msuser.utils.CookieUtils;
import com.lpdm.msuser.utils.OrderUtils;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.lpdm.msuser.utils.shop.ValueType.COOKIE_CART;

@Service
public class OrderServiceImpl implements OrderService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final OrderProxy orderProxy;
    private final ProductService productService;

    @Autowired
    public OrderServiceImpl(OrderProxy orderProxy, ProductService productService) {
        this.orderProxy = orderProxy;
        this.productService = productService;
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
    public Order getOrderFromCookie(HttpServletRequest request) {

        log.info("  |_ Method : getOrderFromCookie");

        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;

        Cart cart = null;
        for(Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_CART))
                cart = CookieUtils.getCartFromCookie(cookie);
        }

        if(cart == null) return null;

        Order order = new Order();
        order.setId(cart.getId());

        int totalProducts = 0;

        for(CookieProduct cookieProduct : cart.getProductList()){

            Product product = productService.findProductById(cookieProduct.getId());

            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setProduct(product);
            orderedProduct.setProductId(product.getId());
            orderedProduct.setQuantity(cookieProduct.getQuantity());
            orderedProduct.setTax(product.getTax());
            orderedProduct.setPrice(product.getPrice());

            totalProducts += orderedProduct.getQuantity();
            order.getOrderedProducts().add(orderedProduct);
        }

        order.setTotalProducts(totalProducts);
        order.setTaxAmount(OrderUtils.getTotalOrderAmount(order));

        log.info("   |_ Order = " + order);

        return order;
    }

    @Override
    public int getOrderIdFromCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        Cart cart = null;
        for(Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_CART))
                cart = CookieUtils.getCartFromCookie(cookie);
        }

        return cart.getId();
    }

    @Override
    public void setOrderToCookie(Order order, HttpServletResponse response) throws JsonProcessingException {

        response.addCookie(CookieUtils.getCookieFromCart(CartUtils.getCartFromOrder(order)));
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

        PaypalUrl paypalUrl = null;

        try { paypalUrl = orderProxy.getPayPalUrl(order, urls); }
        catch (Exception e) { log.warn(e.getMessage()); }

        return paypalUrl;
    }

    @Override
    public String getTransactionDetails(TransactionInfo transactionInfo) {

        return orderProxy.getTransactionDetails(transactionInfo);
    }

    @Override
    public List<Order> findAllByCustomerSorted(int customer, String sort) {

        List<Order> orderList = orderProxy.findAllByCustomerSortedByDate(customer, sort);

        orderList.forEach(o -> o.setTotal((Math.round(o.getTotal() * 100D) / 100D)));

        return orderList;
    }

}
