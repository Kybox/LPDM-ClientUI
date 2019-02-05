package com.lpdm.msuser.proxy;

import com.lpdm.msuser.model.product.Stock;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(name = "${lpdm.zuul.name}", url = "${lpdm.zuul.uri}")
@RibbonClient(name = "${lpdm.stock.name}")
public interface StockProxy {

    @GetMapping(value = "${lpdm.stock.name}/stocks/{id}")
    Stock findStockById(@PathVariable int id);

    @DeleteMapping(value = "${lpdm.stock.name}/stocks/{id}")
    void deleteStockById(@PathVariable int id);

    @PutMapping(value = "${lpdm.stock.name}/stocks")
    Stock updateStock(@RequestBody Stock stock);

    @PostMapping(value = "${lpdm.stock.name}/stocks")
    Stock addNewStock(@RequestBody Stock stock);
}
