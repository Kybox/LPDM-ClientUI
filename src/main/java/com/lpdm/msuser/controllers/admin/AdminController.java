package com.lpdm.msuser.controllers.admin;

import com.lpdm.msuser.services.admin.AdminService;
import com.lpdm.msuser.utils.shop.CustomModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class AdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = "/admin")
    public ModelAndView adminHome(HttpServletRequest request) throws IOException {

        log.info("URL : /admin/");
        log.info("Authorization = " + request.getHeader("Authorization"));


        return CustomModel.getFor("admin/fragments/login", request, true)
                .addObject("pageTitle", "Admin home");
    }

}
