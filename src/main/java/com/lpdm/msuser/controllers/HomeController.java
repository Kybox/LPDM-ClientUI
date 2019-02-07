package com.lpdm.msuser.controllers;

import com.lpdm.msuser.proxy.ProductProxy;
import com.lpdm.msuser.proxy.MsUserProxy;
import com.lpdm.msuser.security.cookie.CookieDecoder;
import com.lpdm.msuser.security.jwt.auth.JwtValidator;
import com.lpdm.msuser.security.jwt.model.JwtUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProductProxy productProxy;

    @Autowired
    MsUserProxy userProxy;

    private final JwtValidator jwtValidator;

    @Autowired
    public HomeController(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    //@Autowired
    //SessionController sessionController;

    /**
     * displays home page
     * @param model
     * @return
     */
    @GetMapping("/")
    public String home(Model model, HttpServletRequest request){
        logger.info("Entrée dans la méthode 'home'");

        JwtUser user = CookieDecoder.getJwtUser(request);
        if(user != null) model.addAttribute("userData", user);

        else logger.info("No valide user in cookie");

        //sessionController.addSessionAttributes(session,model);
        model.addAttribute("categories", productProxy.findAllCategories());
        model.addAttribute("producers", userProxy.getUsersByRole(3));
        return "home";
    }

}
