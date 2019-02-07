package com.lpdm.msuser.controllers;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.proxy.AuthProxy;
import com.lpdm.msuser.proxy.ProductProxy;
import com.lpdm.msuser.proxy.MsUserProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.Model;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage all sesssion information
 */
public class SessionController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProductProxy msProductProxy;

    @Autowired
    AuthProxy msAuthProxy;

    @Autowired
    MsUserProxy msUserProxy;

    public List<OrderedProduct> cart = new ArrayList<>();

    public double cartTotal;


    @Bean
    public SessionController getSession(){
        return new SessionController();
    }

    /**
     * inserts all session data in the model
     * @param session
     * @param model
     */
    public void addSessionAttributes(HttpSession session, Model model){

        try {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
        }catch (NullPointerException e){
            logger.info("Pas d'utilisateur identifié");
        }
        model.addAttribute("cart", cart);
        model.addAttribute("producers", msUserProxy.getUsersByRole(3));
        model.addAttribute("total", cartTotal);
        model.addAttribute("products", msProductProxy.listProduct());
        model.addAttribute("categories", msProductProxy.findAllCategories());
        model.addAttribute("roles", msAuthProxy.findAllRoles());
    }

    /**
     * clears cart
     */
    public void emptyCart(){
        cartTotal = 0;
        cart.clear();
    }

    /**
     * clear all session data
     * @param session
     */
    public void logout(HttpSession session){
        cart.clear();
        cartTotal = 0;
        session.removeAttribute("user");
        session.removeAttribute("total");
    }
}
