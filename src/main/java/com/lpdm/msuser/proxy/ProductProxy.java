package com.lpdm.msuser.proxy;

import com.lpdm.msuser.model.product.Category;
import com.lpdm.msuser.model.product.Product;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient(name = "zuul-server", url = "https://zuul.lpdm.kybox.fr")
@RibbonClient(name = "ms-product")
public interface ProductProxy {

    // Test OK
    @GetMapping(value = "/ms-product/products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Product> listProduct();

    // Test OK
    @GetMapping(value = "ms-product/products/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Product findProduct(@PathVariable int id);

    // Test OK
    @PostMapping(value = "ms-product/products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Product addProduct(@RequestBody Product product);

    @DeleteMapping(value = "ms-product/products/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void deleteProduct(@PathVariable int id);

    // Test OK
    @PutMapping(value = "ms-product/products", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void updateProduct(@RequestBody Product product);

    @GetMapping(value = "ms-product/products/category/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Product> listProductByCategory(@PathVariable int id);

    @PostMapping(value = "ms-product/products/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Product> listProductByCategory2(@RequestBody Category category);

    @GetMapping(value = "ms-product/products/producer/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Product> listProductByProducerId(@PathVariable int id);

    @GetMapping(value = "ms-product/products/name/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Product> listProductByName(@PathVariable String name);

    @GetMapping(value = "ms-product/categories", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Category> listCategories();

    @GetMapping(value = "ms-product/categories/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Category category(@PathVariable int id);

    @PostMapping(value = "ms-product/categories", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void addCategory(@RequestBody Category category);

    @DeleteMapping(value = "ms-product/categories/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void deleteCategory(@PathVariable int id);

    @PutMapping(value = "ms-product/categories", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void updateCategory(@RequestBody Category category);


}
