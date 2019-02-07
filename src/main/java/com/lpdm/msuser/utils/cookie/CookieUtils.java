package com.lpdm.msuser.utils.cookie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpdm.msuser.model.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.IOException;

public class CookieUtils {

    private static Logger log = LoggerFactory.getLogger(CookieUtils.class);

    public static Order getOrderFromCookie(Cookie cookie) throws IOException {

        Order order = new ObjectMapper().readValue(cookie.getValue(), Order.class);

        log.info("CookieUtils Order : " + order);

        return order;
    }

    public static Cookie getCookieFromOrder(Order order) throws JsonProcessingException {

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
}
