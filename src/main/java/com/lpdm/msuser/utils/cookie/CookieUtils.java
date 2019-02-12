package com.lpdm.msuser.utils.cookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.utils.order.OrderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CookieUtils {

    private static Logger log = LoggerFactory.getLogger(CookieUtils.class);

    public static Order getOrderFromCookie(Cookie cookie) throws IOException {

        Order order = new ObjectMapper().readValue(cookie.getValue(), Order.class);

        log.info("CookieUtils Order : " + order);

        return order;
    }

    public static Cookie getCookieFromOrder(Order order) throws JsonProcessingException {

        if(order.getCustomer() != null) {
            order.setCustomerId(order.getCustomer().getId());
            order.setCustomer(new User(order.getCustomerId()));
        }

        for(OrderedProduct orderedProduct : order.getOrderedProducts()) {
            orderedProduct.getProduct().setListStock(null);
            orderedProduct.getProduct().setProducer(null);
            orderedProduct.setPriceWithTax(OrderUtils.getOrderedProductPriceWithTax(orderedProduct));
            orderedProduct.setTotalAmount(OrderUtils.getOrderedProductTotalAmount(orderedProduct));
        }

        ObjectMapper objectMapper =  new ObjectMapper();
        String json = objectMapper.writeValueAsString(order);

        Cookie cookie = new Cookie("order", json);
        cookie.setHttpOnly(true);
        cookie.setPath("/shop");

        log.info("Cookie = " + cookie.getValue());

        return cookie;
    }

    public static Cookie isThereAnOrderFromCookies(Cookie[] cookies){

        for(Cookie cookie : cookies) {
            log.info("Cookie : " + cookie.getName());
            if (cookie.getName().equals("order"))
                return cookie;
        }

        return null;
    }

    public static Order addOrderedProductsFromCookieToOrder(Cookie cookie, Order order) throws IOException {

        Order orderFromCookie = getOrderFromCookie(cookie);
        List<OrderedProduct> productsFromOrder = order.getOrderedProducts();

        for(OrderedProduct orderedProduct : orderFromCookie.getOrderedProducts()) {

            boolean exist = productsFromOrder
                    .stream()
                    .anyMatch(o -> Objects.equals(o.getProduct().getId(), orderedProduct.getProduct().getId()));

            log.info("OrderedProduct already exist ? " + exist);
            if(exist){
                productsFromOrder
                        .stream()
                        .filter(o-> o.getProduct().getId().equals(orderedProduct.getProduct().getId()))
                        .findFirst().ifPresent(o -> o.setQuantity(o.getQuantity() + orderedProduct.getQuantity()));
            }
            else order.getOrderedProducts().add(orderedProduct);
        }

        return order;
    }
}
