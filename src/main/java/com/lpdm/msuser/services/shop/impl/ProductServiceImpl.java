package com.lpdm.msuser.services.shop.impl;

import com.lpdm.msuser.model.product.Category;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.proxy.ProductProxy;
import com.lpdm.msuser.services.shop.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductProxy productProxy;

    @Autowired
    public ProductServiceImpl(ProductProxy productProxy) {
        this.productProxy = productProxy;
    }

    @Override
    public List<Product> findAllProducts() {
        return productProxy.listProduct();
    }

    @Override
    public List<Category> findAllProductCategories() {
        return productProxy.listCategories();
    }
}
