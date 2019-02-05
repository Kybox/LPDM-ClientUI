package com.lpdm.msuser.proxy;

import com.lpdm.msuser.model.auth.UserRole;
import com.lpdm.msuser.model.auth.User;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Component
@FeignClient(name = "${lpdm.zuul.name}", url = "${lpdm.zuul.uri}")
@RibbonClient(name = "${lpdm.auth.name}")
public interface AuthProxy {

    // Search by id
    @RequestMapping(path = "${lpdm.auth.name}/users/{id}",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User findById(@PathVariable(value = "id") int id);

    // Search by name
    @RequestMapping(path = "${lpdm.auth.name}/users/username/{name}",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<User> findByLastName(@PathVariable(value = "name") String name);

    // Search by email
    @RequestMapping(path = "${lpdm.auth.name}/users/email/{email}",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User findByEmail(@PathVariable(value = "email") String name);

    // Search by id and role
    @RequestMapping(path = "${lpdm.auth.name}/roles/check/{userId}/{roleId}",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User findUserByIdAndRole(@PathVariable(value = "userId") int userId,
                             @PathVariable(value = "roleId") int roleId);

    // Get all roles
    @RequestMapping(path = "${lpdm.auth.name}/roles/",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<UserRole> findAllRoles();

    // Add new user
    @RequestMapping(path = "${lpdm.auth.name}/users/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User addNewUser(@RequestBody User user);

    // Update user
    @RequestMapping(path = "${lpdm.auth.name}/users/updateuser",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User updateUser(@RequestBody User user);
}
