package com.lpdm.msuser.controllers;

import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.msauthentication.AppUserBean;
import com.lpdm.msuser.proxies.MsLocationProxy;
import com.lpdm.msuser.proxies.MsProductProxy;
import com.lpdm.msuser.proxies.MsUserProxy;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/identification")
public class LoginController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MsUserProxy msUserProxy;

    @Autowired
    private SessionController sessionController;

    @Autowired
    MsProductProxy msProductProxy;

    @Autowired
    MsLocationProxy msLocationProxy;

    /**
     * displays the login form
     * @param session
     * @param model
     * @return template
     */
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model){
        logger.info("Affichage du formulaire de login");
        sessionController.addSessionAttributes(session, model);
        return "identification/login";
    }

    /**
     * displays the registration form
     * @param session
     * @param model
     * @return template
     */
    @GetMapping("/registration")
    public String registrationForm(HttpSession session, Model model){
        logger.info("Affichage du formulaire d'enregistrement");
        return "identification/registration";
    }

    /**
     * requests ms-auth to identify a user and retrieve their information if password matches. save records in the session
     * @param user
     * @param model
     * @param session
     * @return home template if correct credentials
     */
    @PostMapping("/login")
    public String login(@ModelAttribute AppUserBean user, Model model, HttpSession session){

        logger.info("appUser : " + user.toString());

        logger.info("trying to login");
        AppUserBean appUser = msUserProxy.login(user);

        if (appUser.getId() == 0){
            logger.info("No user found");
            model.addAttribute("error", "Cet utilisateur n'est pas enregistré");
            return "identification/login";

        } else {
            logger.info("appUser : " + appUser.toString());
            logger.info("Entering user in session");
            session.setAttribute("user", appUser);
            sessionController.addSessionAttributes(session, model);
            return "home";
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
            model.addAttribute("error", "Les mots de passe sont différents");
        } else if (msUserProxy.getUserByEmail(user.getEmail()) != null) {
            logger.info("User already exists");
            model.addAttribute("error","L'utilisateur existe déjà");
        }else if(user.getPassword().equals(password2)){
            logger.info("Passwords match!");
            msUserProxy.addUser(user);
            session.setAttribute("user", user);
            sessionController.addSessionAttributes(session, model);
            return "home";
        }
        sessionController.addSessionAttributes(session, model);

        return "/identification/registration";
    }

    /**
     * diconnects the user
     * @param session
     * @return home page template
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        sessionController.logout(session);
        return "home";
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable ("id") int id, Model model, HttpSession session){
        logger.info("entering profile");
        AppUserBean user = msUserProxy.getUserById(id);
        model.addAttribute("user", user);

        sessionController.addSessionAttributes(session, model);
        return "users/profile";
    }

    @GetMapping("/edit/{id}")
    public String editProfileForm(@PathVariable("id") int id, Model model, HttpSession session){
        logger.info("entering profile");
        AppUserBean user = msUserProxy.getUserById(id);
        model.addAttribute("user", user);
        sessionController.addSessionAttributes(session, model);
        return "users/useredit";
    }


    @PostMapping(value = "/edit")
    public String changeProfile(@ModelAttribute AppUserBean userToUpdate, Model model, HttpSession session){
        logger.info("changeProfile userToUpdate: " + userToUpdate.toString());
        AppUserBean sessionUser = (AppUserBean)session.getAttribute("user");
        userToUpdate.setId(sessionUser.getId());
        AppUserBean appUser = msUserProxy.updateAppUser(userToUpdate);
        model.addAttribute("user", appUser);
        sessionController.addSessionAttributes(session, model);
        return "users/profile";
    }

    @PostMapping(value = "/editroles")
    public String changeRoles(@RequestParam List<String> appRole, Model model, HttpSession session){
        AppUserBean sessionUser = (AppUserBean)session.getAttribute("user");

        sessionUser.getAppRole().clear();

        for(String role: appRole)
            sessionUser.getAppRole().add(msUserProxy.getRoleById(Integer.parseInt(role)));
        msUserProxy.updateAppUser(sessionUser);
        session.setAttribute("user", sessionUser);
        sessionController.addSessionAttributes(session, model);
        return "users/profile";
    }

}
