package com.lpdm.msuser.controllers.shop;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.model.order.*;
import com.lpdm.msuser.model.paypal.TransactionInfo;
import com.lpdm.msuser.services.shop.*;
import com.lpdm.msuser.utils.cookie.CookieUtils;
import com.lpdm.msuser.utils.shop.CustomModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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

        return CustomModel.getFor("/shop/fragments/order/order_process", request, false)
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

        log.info("Method : orderProcess2");

        List<Delivery> deliveryList = orderService.findAllDeliveryMethods();

        return CustomModel.getFor("/shop/fragments/order/order_process", request, false)
                .addObject("orderProcess", 2)
                .addObject("deliveryList", deliveryList);
    }

    @PostMapping("/shop/order/process/2")
    public Order updateDeliveryMethod(@RequestBody Delivery delivery, HttpServletRequest request) throws IOException {

        log.info("Method : updateDeliveryMethod");

        Order order = orderService.getOrderById(orderService.getOrderIdFromCookie(request));
        order.setDelivery(delivery);

        log.info(" |_ Order = " + order);

        return orderService.saveOrder(order);
    }

    @GetMapping("/shop/order/process/3")
    public ModelAndView orderProcess3(HttpServletRequest request) throws IOException {

        log.info("Method : orderProcess3");

        return CustomModel.getFor("shop/fragments/order/order_process", request, false)
                .addObject("orderProcess", 3)
                .addObject("paymentList", orderService.findAllPayments());
    }

    @PostMapping("/shop/order/process/3")
    public Order updatePaymentMethod(@RequestBody Payment payment,
                                            HttpServletRequest request) throws IOException {

        Order order = orderService.getOrderById(orderService.getOrderIdFromCookie(request));
        order.setPayment(payment);

        return orderService.saveOrder(order);
    }

    @GetMapping("/shop/order/process/4")
    public ModelAndView orderProcess4(HttpServletRequest request) throws IOException {

        return CustomModel.getFor("shop/fragments/order/order_process", request, false)
                .addObject("orderProcess", 4);
    }

    @GetMapping("/shop/order/process/5")
    public ModelAndView orderProcess5(HttpServletRequest request,
                                      HttpServletResponse response)
            throws IOException {

        SuccessUrl urls = new SuccessUrl();
        urls.setReturnUrl("http://localhost:30000/shop/order/process/success");
        urls.setCancelUrl("http://localhost:30000/shop/order/process/error");

        Order order = orderService.getOrderById(orderService.getOrderIdFromCookie(request));

        PaypalUrl paypalUrl = orderService.getPaypalPaymentUrl(order.getId(), urls);

        for(Map.Entry<String, String> header : paypalUrl.getHeaders().entrySet())
            response.setHeader(header.getKey(), header.getValue());

        String redirectUrl = paypalUrl.getRedirectUrl();

        ModelAndView modelAndView;
        if(redirectUrl == null) modelAndView = new ModelAndView("/orders/paypalcancel");
        else modelAndView = new ModelAndView( "redirect:" + paypalUrl.getRedirectUrl());

        return modelAndView;
    }

    @GetMapping("/shop/order/process/success")
    public ModelAndView orderPaymentSuccess(@RequestParam Map<String, String> params,
                                            HttpServletRequest request) throws IOException {

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPayerID(params.get("PayerId"));
        transactionInfo.setPaymentId(params.get("paymentId"));
        transactionInfo.setToken(params.get("token"));

        log.info("Transaction info = " + transactionInfo);

        Order order = orderService.getOrderById(orderService.getOrderIdFromCookie(request));

        log.info("Oder = " + order);

        order.setStatus(Status.PAID);

        orderService.saveOrder(order);

        CookieUtils.removeOrderFromCookie(request);

        return CustomModel.getFor("/shop/fragments/order/order_payment_success", request, true);
    }

    @GetMapping("shop/order/payment/cancel")
    public String cancelPaypal(){
        return "orders/paypalcancel";
    }
}
