package com.lpdm.msuser.proxy;

import com.lpdm.msuser.model.auth.UserRole;
import com.lpdm.msuser.model.auth.User;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient(name = "zuul-server", url = "https://zuul.lpdm.kybox.fr")
@RibbonClient(name = "microservice-authentication")
public interface MsUserProxy {

    @GetMapping("/microservice-authentication/users/")
    List<User> getAllUsers();

    @GetMapping(value = "/microservice-authentication/users/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User getUserById(@PathVariable("id") int id);

    @PostMapping(value = "/microservice-authentication/users/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User login(@RequestBody User user);

    @PostMapping(value = "/microservice-authentication/users/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User addUser(@RequestBody User user);

    @PostMapping(value = "/microservice-authentication/roles/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    UserRole addRole(@RequestBody UserRole role);

    @PostMapping(value = "/microservice-authentication/users/get", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User getUserByUsername(@RequestParam String username);

    @GetMapping(value = "/microservice-authentication/users/email/{email}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User getUserByEmail(@PathVariable String email);

    @GetMapping(value = "/microservice-authentication/users/per_role/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<User>getUsersByRole(@PathVariable("id") int id);

    @GetMapping(value = "/microservice-authentication/roles/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    UserRole getRoleById(@PathVariable("id") int id);

    @PutMapping(value = "/microservice-authentication/users/updateuser", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User updateAppUser(@RequestBody User user);

    @PutMapping(value = "/microservice-authentication/password/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Boolean updatePassword(@PathVariable("id") int id, @RequestParam String oldPassword, @RequestParam String newPassword);
}
