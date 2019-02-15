package com.lpdm.msuser.controllers.shop;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.location.City;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.Status;
import com.lpdm.msuser.model.shop.LoginForm;
import com.lpdm.msuser.security.cookie.CookieAppender;
import com.lpdm.msuser.security.cookie.JwtCookieRemover;
import com.lpdm.msuser.security.jwt.auth.JwtGenerator;
import com.lpdm.msuser.security.jwt.auth.JwtUserBuilder;
import com.lpdm.msuser.security.jwt.model.JwtUser;
import com.lpdm.msuser.services.shop.AuthService;
import com.lpdm.msuser.services.shop.LocationService;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.services.shop.SecurityService;
import com.lpdm.msuser.utils.CookieUtils;
import com.lpdm.msuser.utils.OrderUtils;
import com.lpdm.msuser.utils.shop.CustomModel;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final AuthService authService;
    private final JwtGenerator jwtGenerator;
    private final SecurityService securityService;
    private final LocationService locationService;
    private final OrderService orderService;

    @Autowired
    public AuthController(AuthService authService,
                          JwtGenerator jwtGenerator,
                          SecurityService securityService,
                          LocationService locationService,
                          OrderService orderService) {

        this.authService = authService;
        this.jwtGenerator = jwtGenerator;
        this.securityService = securityService;
        this.locationService = locationService;
        this.orderService = orderService;
    }

    @GetMapping(value = "/shop/account/logout")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JwtCookieRemover.remove(response);

        return CustomModel.getFor("redirect:/shop", request, true);
    }

    @GetMapping(value = "/shop/login")
    public ModelAndView loginPage(HttpServletRequest request) throws IOException {

        return CustomModel.getFor("/shop/fragments/account/login", request, true)
                .addObject("loginForm", new LoginForm());
    }

    @PostMapping(value = "/shop/login")
    public ModelAndView loginUser(@ModelAttribute LoginForm loginForm,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException {

        log.info("LoginForm : " + loginForm);

        User user = null;
        String loginError = null;
        try{
            user = authService.loginUser(loginForm);
        }
        catch (FeignException e){
            loginError = e.getLocalizedMessage();
        }

        log.info("User : " + user);

        ModelAndView modelAndView;

        if(user != null){

            log.info("-> Redirect account");

            JwtUser jwtUser = JwtUserBuilder.build(user);
            CookieAppender.addToken(jwtGenerator.generate(jwtUser), response);

            modelAndView = CustomModel.getFor("redirect:/shop/account", request, true);
        }
        else {

            log.info("-> Redirect login");

            modelAndView = CustomModel.getFor("redirect:/shop/login", request, true)
                    .addObject("loginForm", new LoginForm())
                    .addObject("loginError", loginError);
        }

        return modelAndView;
    }

    @SuppressWarnings("Duplicates")
    @GetMapping(value = "/shop/account")
    public ModelAndView accountDefault(HttpServletRequest request,
                                       HttpServletResponse response)
            throws IOException {

        User user = securityService.getAuthenticatedUser(request);

        ModelAndView modelAndView;
        if(user != null){

            Order order = orderService.findLastOrderByCustomerAndStatus(user.getId(), Status.VALIDATED.getId());

            if(order != null) {

                modelAndView = CustomModel.getFor("shop/fragments/account/account", request, false);
                modelAndView.addObject("accountContent", "order");
                modelAndView.addObject("cookieOrder", OrderUtils.setAllAmountsFromOrder(order));
            }
            else {
                modelAndView = CustomModel.getFor("shop/fragments/account/account", request, true);
                modelAndView.addObject("accountContent", "cart");
            }
        }
        else{

            log.info("-> User is null, redirect login");

            modelAndView = CustomModel.getFor("redirect:/shop/login", request, true)
                    .addObject("loginForm", new LoginForm());
        }

        return modelAndView;
    }

    @PostMapping(value = {"/shop/address/cities", "/search/address/cities/"})
    public List<City> searchAddressResult(@RequestParam Map<String, String> data){

        log.info("Search cities for zipCode " + data.get("zipCode"));
        return locationService.findCitiesByZipCode(data.get("zipCode"));
    }

    @GetMapping(value = "/account/delete/order/{id}")
    public ModelAndView cancelOrder(@PathVariable int id,
                                    HttpServletRequest request,
                                    HttpServletResponse response){

        orderService.cancelOrderById(id);
        CookieUtils.removeOrderFromCookie(request, response);

        return new ModelAndView("redirect:/shop/account");
    }

    @GetMapping(value = "/shop/account/infos")
    public ModelAndView userInfos(HttpServletRequest request) throws IOException {

        User user = securityService.getAuthenticatedUser(request);

        return CustomModel.getFor("/shop/fragments/account/account", request, true)
                .addObject("accountContent", "infos")
                .addObject("user", user);
    }
}
