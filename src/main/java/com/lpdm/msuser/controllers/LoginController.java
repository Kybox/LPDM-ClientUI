package com.lpdm.msuser.controllers;

import com.lpdm.msuser.msauthentication.AppUserBean;
import com.lpdm.msuser.proxies.MsProductProxy;
import com.lpdm.msuser.proxies.MsUserProxy;
import com.lpdm.msuser.security.cookie.CookieAppender;
import com.lpdm.msuser.security.jwt.auth.JwtGenerator;
import com.lpdm.msuser.security.jwt.auth.JwtUserBuilder;
import com.lpdm.msuser.security.jwt.config.JwtAuthConfig;
import com.lpdm.msuser.security.jwt.model.JwtRedirect;
import com.lpdm.msuser.security.jwt.model.JwtUser;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/identification")
public class LoginController {


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MsUserProxy msUserProxy;

    //@Autowired
    //private SessionController sessionController;

    @Autowired
    MsProductProxy msProductProxy;

    private final JwtAuthConfig jwtAuthConfig;
    private final JwtGenerator jwtGenerator;

    @Autowired
    public LoginController(JwtGenerator jwtGenerator, JwtAuthConfig jwtAuthConfig) {
        this.jwtGenerator = jwtGenerator;
        this.jwtAuthConfig = jwtAuthConfig;
    }

    /**
     * displays the login form
     * @param session
     * @param model
     * @return template
     */
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String page, Model model){
        logger.info("Affichage du formulaire de login");
        logger.info("-> Path = " + page);
        //sessionController.addSessionAttributes(session, model);
        //return "identification/login";
        if(page != null) model.addAttribute("redirect", new JwtRedirect(page));
        model.addAttribute("appUser", new AppUserBean());
        return "shop/fragments/account/login";
    }

    /**
     * displays the registration form
     * @param model
     * @return template
     */
    @GetMapping("/registration")
    public String registrationForm(Model model){
        logger.info("Affichage du formulaire d'enregistrement");


        return "identification/registration";
    }

    /**
     * requests ms-auth to identify a user and retrieve their information if password matches. save records in the session
     * @param user
     * @param model

     * @return home template if correct credentials
     */
    @PostMapping("/login")
    public String login(@ModelAttribute AppUserBean user,
                        @ModelAttribute JwtRedirect redirect,
                        Model model, HttpServletResponse response) {

        logger.info("Redirect value = " + redirect.getPath());
        logger.info("appUser : " + user.toString());
        logger.info("Essai de login");

        AppUserBean appUser = null;

        try{ appUser = msUserProxy.login(user); }
        catch (FeignException e){

            logger.warn(e.getMessage());
            model.addAttribute("error", "Cet utilisateur n'est pas enregistré");
            return "identification/login";
        }

        logger.info("appUser : " + appUser.toString());

        if (appUser.getId() == 0){
            logger.info("Pas d'utilisateur trouvé");

            return "identification/login";

        } else {
            logger.info("appUser : " + appUser.toString());
            logger.info("Entrée de l'utilisateur dans la session");
            //session.setAttribute("user", appUser);
            //sessionController.addSessionAttributes(session, model);

            /* USER FOUND, NOW GENERATE THE JWT USER AND INJECT IT TO A COOKIE*/
            JwtUser jwtUser = JwtUserBuilder.build(appUser);
            CookieAppender.addToken(jwtGenerator.generate(jwtUser), response);

            if(redirect.getPath() != null) return "redirect:/" + redirect.getPath();
            else return "redirect:/";
        }
    }

    /**
     * sends information to ms-auth to persist a new user and open an account after password confirmation. Save records in the session
     * @param user
     * @param password2
     * @param model
     * @param session
     * @param bindingResult
     * @return home template
     */
    @PostMapping("/registration")
    public String registration(@ModelAttribute AppUserBean user, @RequestParam String password2, Model model, HttpSession session, BindingResult bindingResult){

        logger.info("Essai de registration");
        logger.info("Utilisateur: " + user.getFirstName() + " " + user.getName() + " " + user.getEmail() + " " + user.getPassword());

        if (bindingResult.hasErrors()) {
            return "/identification/registration";
        }
        if (!user.getPassword().equals(password2)){
            logger.info("erreur de mdp: " + password2);
            model.addAttribute("error", "Les mots de passe saisis sont différents");
        } else if (msUserProxy.getUserByEmail(user.getEmail()) != null) {
            logger.info("L'utilisateur existe déjà");
            model.addAttribute("error","L'utilisateur existe déjà");
        }else if(user.getPassword().equals(password2)){
            logger.info("Mot de passe similaires");
            msUserProxy.addUser(user);
            session.setAttribute("user", user);
            //sessionController.addSessionAttributes(session, model);
            return "home";
        }
        //sessionController.addSessionAttributes(session, model);

        return "/identification/registration";
    }

    /**
     * diconnects the user
     * @param session
     * @return home page template
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        //sessionController.logout(session);
        return "home";
    }

}
