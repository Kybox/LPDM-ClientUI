package com.lpdm.msuser.services.shop;

import com.lpdm.msuser.model.product.Category;
import com.lpdm.msuser.model.product.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAllProducts();
    List<Category> findAllProductCategories();
}
