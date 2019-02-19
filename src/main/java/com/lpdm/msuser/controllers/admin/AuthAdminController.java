package com.lpdm.msuser.controllers.admin;

import com.lpdm.msuser.model.admin.SearchForm;
import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.model.location.City;
import com.lpdm.msuser.services.admin.AdminService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.lpdm.msuser.utils.admin.ValueType.*;

@RestController
@RequestMapping(AUTH_ADMIN_PATH)
public class AuthAdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final AdminService adminService;

    @Autowired
    public AuthAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = DEFAULT_PATH)
    public ModelAndView adminAuth(){
        return new ModelAndView(AUTH_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, AUTH_PAGE_TITLE);
    }

    @GetMapping(value = {"/update", "/update/"})
    public ModelAndView searchUser(){
        return new ModelAndView(AUTH_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, AUTH_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_UPDATE_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @PostMapping(value = {"/update", "/update/"})
    public ModelAndView searchUserResult(
            @Valid @ModelAttribute(HTML_PAGE_SEARCH_FORM) SearchForm searchForm){

        int searchValue = searchForm.getSearchValue();
        String keyword = searchForm.getKeyword();
        Object result = null;

        try{
            switch (searchValue){

                case SEARCH_USER_BY_ID:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches())
                        result = adminService.findUserById(Integer.parseInt(keyword));
                    else result = 500;
                    break;

                case SEARCH_USER_BY_NAME:
                    result = adminService.findUserByLastName(keyword);
                    break;

                case SEARCH_USER_BY_EMAIL:
                    result = adminService.findUserByEmail(keyword);
                    break;
            }
        }
        catch (FeignException e ){
            log.warn(e.getMessage());
            result = e.status();
        }

        return new ModelAndView(AUTH_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, AUTH_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_UPDATE_PAGE)
                .addObject(HTML_RESULT_OBJECT, result)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm())
                .addObject("roleList", adminService.findAllUserRoles());
    }

    @PostMapping(value = {"/update/user", "/update/user/"})
    public User updateUser(@Valid @RequestBody User user){

        if(user.getRegistrationDate() == null) user.setRegistrationDate(LocalDateTime.now());
        log.info("User : " + user);
        return adminService.updateUser(user);
    }

    @GetMapping(value = {"/add", "/add/"})
    public ModelAndView addUser(){
        return new ModelAndView(AUTH_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, AUTH_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_ADD_PAGE)
                .addObject("roleList", adminService.findAllUserRoles());
    }

    @PostMapping(value = {"/add", "/add/"})
    public User addNewUser(@Valid @RequestBody User user){

        user.setRegistrationDate(LocalDateTime.now());
        log.info("User : " + user);
        return adminService.addNewUser(user);
    }

    @GetMapping(value = {"/search/address", "/search/address/"})
    public ModelAndView searchUserForAddress(){
        return new ModelAndView(AUTH_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, AUTH_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_ADDRESS_MANAGEMENT_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @PostMapping(value = {"/search/address", "/search/address/"})
    public ModelAndView searchAddressResult(
            @Valid @ModelAttribute(HTML_PAGE_SEARCH_FORM) SearchForm searchForm){

        int searchValue = searchForm.getSearchValue();
        String keyword = searchForm.getKeyword();
        List<Address> addressList = null;
        Object result = null;
        try{
            switch (searchValue){

                case SEARCH_USER_BY_ID:
                    log.info("Search user by ID");
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches())
                        result = adminService.findUserById(Integer.parseInt(keyword));
                    else result = 500;
                    break;

                case SEARCH_USER_BY_NAME:
                    log.info("Search user by lastName");
                    result = adminService.findUserByLastName(keyword);
                    break;
            }

            List<User> userList = new ArrayList<>((List<User>) result);
            if(result instanceof ArrayList) {
                addressList = new ArrayList<>();

                for(User user : userList){

                    try{
                        addressList.add(adminService.findAddressById(user.getAddressId()));
                    }
                    catch (FeignException e){
                        log.warn(e.getMessage());
                        addressList.add(null);
                    }
                }
            }
        }
        catch (FeignException e ){
            log.warn(e.getMessage());
            result = e.status();
        }

        return new ModelAndView(AUTH_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, AUTH_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_ADDRESS_MANAGEMENT_PAGE)
                .addObject(HTML_RESULT_OBJECT, result)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm())
                .addObject("addressList", addressList);
    }

    @PostMapping(value = {"/search/address/cities", "/search/address/cities/"})
    public List<City> searchAddressResult(@RequestParam Map<String, String> data){

        log.info("Search cities for zipCode " + data.get("zipCode"));
        return adminService.findCitiesByZipCode(data.get("zipCode"));
    }

    @PostMapping(value = {"/add/address", "/add/address/"})
    public Address addNewAddress(@RequestParam int user, @Valid @RequestBody Address address){

        address = adminService.saveNewAddress(address, user);
        return address;
    }
}
