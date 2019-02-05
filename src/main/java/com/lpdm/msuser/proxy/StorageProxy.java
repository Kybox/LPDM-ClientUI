package com.lpdm.msuser.proxy;

import com.lpdm.msuser.model.storage.Storage;
import com.lpdm.msuser.model.admin.StorageUser;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "${lpdm.zuul.name}", url = "${lpdm.zuul.uri}")
@RibbonClient(name = "${lpdm.storage.name}")
public interface StorageProxy {

    @PostMapping(value = "${lpdm.storage.name}/",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String getUploadForm(@RequestBody StorageUser user);

    @GetMapping(value = "${lpdm.storage.name}/user/{id}/latest",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Storage getLatestFileUploadByOwner(@PathVariable int id);
}
