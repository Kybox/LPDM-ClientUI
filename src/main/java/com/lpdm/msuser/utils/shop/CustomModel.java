package com.lpdm.msuser.utils.shop;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.services.shop.CartService;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.services.shop.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CustomModel {

    private static Logger log = LoggerFactory.getLogger(CustomModel.class);

    private static CartService cartService;
    private static OrderService orderService;
    private static SecurityService securityService;

    @Autowired
    public CustomModel(CartService cartService,
                       OrderService orderService,
                       SecurityService securityService) {

        CustomModel.cartService = cartService;
        CustomModel.orderService = orderService;
        CustomModel.securityService = securityService;
    }

    public static ModelAndView getFor(String url, HttpServletRequest request) throws IOException {

        Order order = cartService.getCartFormCookie(request);

        if(order != null){

            if(order.getId() != 0) {
                order = orderService.getOrderById(order.getId());
                order.setCustomer(new User(order.getCustomer().getId()));
            }

            for(OrderedProduct orderedProduct : order.getOrderedProducts()) {
                orderedProduct.setPriceWithTax(orderService.getOrderedProductPriceWithTax(orderedProduct));
                orderedProduct.setTotalAmount(orderService.getOrderedProductTotalAmount(orderedProduct));
                orderedProduct.getProduct().setListStock(null);
            }

            log.info("Custom model Order = " + order);

            int total = orderService.getTotalOrderedProducts(order);

            order.setTotal(orderService.getTotalOrderAmount(order));

            ModelAndView modelAndView =  new ModelAndView(url)
                    .addObject("cookieOrder", order)
                    .addObject("totalProducts", total);

            User user = securityService.getAuthenticatedUser(request);

            if(user != null)
                modelAndView.addObject("user", user);

            return modelAndView;
        }

        log.info("No order found in the cookies");

        return new ModelAndView(url);
    }
}
