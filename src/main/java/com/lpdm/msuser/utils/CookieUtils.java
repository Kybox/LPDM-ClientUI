package com.lpdm.msuser.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.model.shop.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.lpdm.msuser.utils.shop.ValueType.*;

public class CookieUtils {

    private static Logger log = LoggerFactory.getLogger(CookieUtils.class);

    public static Order getOrderFromCookie(Cookie cookie) throws IOException {

        log.info("Method : getOrderFromCookie");

        Order order = null;

        try { order = new ObjectMapper().readValue(cookie.getValue(), Order.class); }
        catch (Exception e) { log.warn(e.getMessage()); }

        log.info(" |_ Order = " + order);

        return order;
    }

    public static Cart getCartFromCookie(Cookie cookie){

        log.info("Method : getCartFromCookie");

        Cart cart = null;
        try { cart = new ObjectMapper().readValue(cookie.getValue(), Cart.class); }
        catch (Exception e) { log.warn(e.getMessage()); }

        log.info(" |_ Cart = " + cart);

        return cart;
    }

    public static Cookie getCookieFromCart(Cart cart) throws JsonProcessingException {

        log.info("Method : getCookieFromCart");

        ObjectMapper objectMapper =  new ObjectMapper();
        String jsonObj = objectMapper.writeValueAsString(cart);

        Cookie cookie = new Cookie(COOKIE_CART, jsonObj);
        cookie.setHttpOnly(true);
        cookie.setPath("/shop");

        log.info(" |_ Cookie = " + cookie.getValue());

        return cookie;

        /*
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

        Cookie cookie = new Cookie(COOKIE_CART, json);
        cookie.setHttpOnly(true);
        cookie.setPath("/shop");

        log.info(" |_ Cookie = " + cookie.getValue());

        return cookie;

        */
    }

    public static Cookie isThereATempOrderFromCookies(Cookie[] cookies){

        log.info("Method : isThereATempOrderFromCookies");

        for(Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_CART)) {
                log.info(" |_ Cart from cookies found");
                return cookie;
            }
        }

        return null;
    }

    public static Order addOrderedProductsFromCookieToOrder(Cookie cookie, Order order) throws IOException {

        log.info("Method : addOrderedProductsFromCookieToOrder");

        Order orderFromCookie = getOrderFromCookie(cookie);

        if(orderFromCookie.getId() == 0){

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
        }

        return order;
    }

    public static Cookie removeOrderFromCookie(HttpServletRequest request,
                                               HttpServletResponse response){

        log.info("Method : removeOrderFromCookie");

        Cookie emptyCookie = null;
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_CART)) {
                    cookie.setValue("");
                    emptyCookie = cookie;
                    emptyCookie.setPath("/shop");
                    emptyCookie.setHttpOnly(true);
                    response.addCookie(emptyCookie);
                }
            }
        }

        return emptyCookie;
    }
}
