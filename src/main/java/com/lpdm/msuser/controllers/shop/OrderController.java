package com.lpdm.msuser.controllers.shop;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.model.order.Delivery;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.PaypalUrl;
import com.lpdm.msuser.model.order.SuccessUrl;
import com.lpdm.msuser.services.shop.*;
import com.lpdm.msuser.utils.shop.CustomModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final LocationService locationService;
    private final CartService cartService;
    private final SecurityService securityService;
    private final AuthService authService;
    private final OrderService orderService;

    @Autowired
    public OrderController(CartService cartService,
                           SecurityService securityService,
                           LocationService locationService,
                           AuthService authService, OrderService orderService) {
        this.cartService = cartService;
        this.securityService = securityService;
        this.locationService = locationService;
        this.authService = authService;
        this.orderService = orderService;
    }

    @GetMapping("shop/order/process/1")
    public ModelAndView orderProcess1(HttpServletRequest request) throws IOException {

        User user = securityService.getAuthenticatedUser(request);
        boolean userAddress = user.getAddress() != null;

        return CustomModel.getFor("/shop/fragments/order/order_process", request)
                .addObject("user", user)
                .addObject("userAddress", userAddress)
                .addObject("orderProcess", 1);
    }

    @PostMapping("/shop/order/process/1")
    public Address addNewAddress(@RequestBody Address address, HttpServletRequest request) throws IOException {

        address = locationService.addNewAddress(address);

        User user = securityService.getAuthenticatedUser(request);

        user.setAddressId(address.getId());
        authService.updateUser(user);

        return address;
    }

    @PutMapping("/shop/order/process/1")
    public Address updateAddress(@RequestBody Address address, HttpServletRequest request){

        User user = securityService.getAuthenticatedUser(request);

        Address oldAddress = locationService.findAddressById(user.getAddressId());

        log.info("Old Address = " + oldAddress);

        address.setId(oldAddress.getId());
        if(address.getCity().getId() == null)
            address.getCity().setId(oldAddress.getCity().getId());

        log.info("New address = " + address);

        return locationService.updateAddress(address);
    }

    @GetMapping("/shop/order/process/2")
    public ModelAndView orderProcess2(HttpServletRequest request) throws IOException {

        List<Delivery> deliveryList = orderService.findAllDeliveryMethods();

        return CustomModel.getFor("/shop/fragments/order/order_process", request)
                .addObject("orderProcess", 2)
                .addObject("deliveryList", deliveryList);
    }

    @PostMapping("/shop/order/process/2")
    public Order updateDeliveryMethod(@RequestBody Delivery delivery, HttpServletRequest request) throws IOException {

        Order order = cartService.getCartFormCookie(request);
        order = orderService.getOrderById(order.getId());
        order.setDelivery(delivery);

        return orderService.saveOrder(order);
    }

    @GetMapping("/shop/order/process/3")
    public ModelAndView orderProcess3(HttpServletRequest request) throws IOException {

        return CustomModel.getFor("shop/fragments/order/order_process", request)
                .addObject("orderProcess", 3)
                .addObject("paymentList", orderService.findAllPayments());
    }

    @GetMapping("/shop/order/process/4")
    public ModelAndView orderProcess4(HttpServletRequest request){

        return new ModelAndView();
    }

    @GetMapping("/paypalsuccess")
    public String successPaypal(){
        return "orders/paypalsuccess";
    }

    @GetMapping("/paypalcancel")
    public String cancelPaypal(){
        return "orders/paypalcancel";
    }


    @GetMapping("/confirmorder/{id}")
    public String confirmOrder(@PathVariable("id") int id, HttpServletResponse response){

        SuccessUrl successUrl = new SuccessUrl();
        successUrl.setReturnUrl("https://lpdm.kybox.fr/orders/paypalsuccess");
        successUrl.setCancelUrl("https://lpdm.kybox.fr/orders/paypalcancel");

        PaypalUrl paypalUrl = null;

        for (Map.Entry<String, String> header: paypalUrl.getHeaders().entrySet()) {
            response.setHeader(header.getKey(), header.getValue());
        }


        return "redirect:" + paypalUrl.getRedirectUrl();
    }
}
