package com.lpdm.msuser.services.shop;

import com.lpdm.msuser.model.product.Category;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.model.shop.ProductsPageable;

import java.util.List;

public interface ProductService {

    List<Category> findAllCategories();

    ProductsPageable findProductPageable(int page, int size);

    ProductsPageable findProductPromotions(int page, int size);

    List<Product> findProductsByCategory(int category);

    String getCategoryName(int category);

    Product findProductById(int id);

    int coundProductQuatity(Product product);
}
