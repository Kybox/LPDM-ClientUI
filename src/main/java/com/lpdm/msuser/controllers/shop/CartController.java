package com.lpdm.msuser.controllers.shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.model.order.Status;
import com.lpdm.msuser.model.shop.LoginForm;
import com.lpdm.msuser.services.shop.CartService;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.services.shop.SecurityService;
import com.lpdm.msuser.utils.shop.CustomModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class CartController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final CartService cartService;
    private final SecurityService securityService;
    private final OrderService orderService;

    @Autowired
    public CartController(CartService cartService,
                          SecurityService securityService,
                          OrderService orderService) {
        this.cartService = cartService;
        this.securityService = securityService;
        this.orderService = orderService;
    }

    @PostMapping(value = "/shop/cart/add")
    public Order addProductToCart(@RequestBody OrderedProduct orderedProduct,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws JsonProcessingException {

        return cartService.addProductToCart(orderedProduct, request, response);
    }

    @GetMapping(value = "/shop/cart")
    public ModelAndView cartOverview(HttpServletRequest request) throws IOException {

        User user = securityService.getAuthenticatedUser(request);

        ModelAndView modelAndView = null;

        if(user != null){


            modelAndView = CustomModel.getFor("shop/fragments/cart/view", request);
        }
        else{

            modelAndView = CustomModel.getFor("/shop/fragments/account/login", request)
                    .addObject("loginForm", new LoginForm());
        }

        return modelAndView;
    }

    @PostMapping(value = "/shop/cart/quantity/{mode}")
    public Order updateQuantity(@ModelAttribute(value = "id") int productId,
                                         @PathVariable String mode,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {

        log.info("Id : " + productId);

        return cartService.updateQuantity(productId, mode, request, response);
    }

    @DeleteMapping(value = "/shop/cart/delete")
    public Order deleteProduct(@ModelAttribute(value = "id") int productId,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException {

        return cartService.deleteProductFromCart(productId, request, response);
    }

    @GetMapping(value = "/shop/cart/save")
    public ModelAndView saveCart(HttpServletRequest request, HttpServletResponse response) throws IOException {


        Order order = cartService.getCartFormCookie(request);
        User user = securityService.getAuthenticatedUser(request);

        order.setCustomer(user);

        if(order.getStatus() == null)
            order.setStatus(Status.VALIDATED);

        if(order.getOrderDate() == null)
            order.setOrderDate(LocalDateTime.now());

        order = orderService.saveOrder(order);

        log.info("Order saved : " + order);

        orderService.setOrderToCookie(order, response);

        return CustomModel.getFor("/shop/fragments/account/account", request)
                .addObject("accountContent", "order");
    }
}
