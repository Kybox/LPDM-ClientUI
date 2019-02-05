package com.lpdm.msuser.proxy;

import com.lpdm.msuser.model.store.Store;
import feign.FeignException;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient(name = "${lpdm.zuul.name}", url = "${lpdm.zuul.uri}")
@RibbonClient(name = "${lpdm.store.name}")
public interface StoreProxy {

    @RequestMapping(path = "${lpdm.store.name}/stores/{id}",
            method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Store findById(@PathVariable(value = "id") int id) throws FeignException;

    @RequestMapping(path = "${lpdm.store.name}/stores/name/{name}",
            method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Store> findByName(@PathVariable(value = "name") String name) throws FeignException;

    @PutMapping(path = "${lpdm.store.name}/admin/update",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Store updateStore(@RequestBody Store store);

    @PostMapping(path = "${lpdm.store.name}/admin/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Store addNewStore(@RequestBody Store store);
}
