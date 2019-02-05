package com.lpdm.msuser.proxy;

import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.model.location.City;
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
@RibbonClient(name = "${lpdm.location.name}")
public interface LocationProxy {

    @RequestMapping(path = "${lpdm.location.name}/address/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Address findAddressById(@PathVariable int id);

    @RequestMapping(path = "${lpdm.location.name}/cities/zipcode/{zipCode}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<City> findCitiesByZipCode(@PathVariable String zipCode);

    @RequestMapping(path = "${lpdm.location.name}/address",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Address saveNewAddress(@RequestBody Address address);
}
