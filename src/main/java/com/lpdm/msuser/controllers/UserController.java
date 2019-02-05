package com.lpdm.msuser.controllers;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.proxy.LocationProxy;
import com.lpdm.msuser.proxy.MsUserProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private MsUserProxy msUserProxy;

    @Autowired
    SessionController sessionController;

    @Autowired
    LocationProxy msLocationProxy;

    /**
     * lists all users and their data
     * @param model
     * @param session
     * @return template with list of users
     */
    @GetMapping("/list")
    public String allUsers(Model model, HttpSession session){
        List<User> appUsers = msUserProxy.getAllUsers();
        model.addAttribute("users", appUsers);
        sessionController.addSessionAttributes(session, model);
        return "users/list";
    }

    /**
     * displays the user information
     * @param id
     * @param model
     * @param session
     * @return user description
     */
    @GetMapping("/{id}")
    public String userDescription(@PathVariable ("id") int id, Model model, HttpSession session){
        User user = msUserProxy.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("address", msLocationProxy.findAddressById(user.getId()));
        sessionController.addSessionAttributes(session, model);
        return "users/userdescription";
    }

    /**
     * requests msUserProxy to persist a user in the DB
     * @param user
     * @param model
     * @param session
     * @return
     */
    @PostMapping("/")
    public String addUser(@ModelAttribute User user, Model model, HttpSession session){
        msUserProxy.addUser(user);
        sessionController.addSessionAttributes(session, model);
        return "users/list";
    }

}
