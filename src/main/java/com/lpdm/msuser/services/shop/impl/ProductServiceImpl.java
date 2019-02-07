package com.lpdm.msuser.services.shop.impl;

import com.lpdm.msuser.model.product.Category;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.model.product.Stock;
import com.lpdm.msuser.model.shop.ProductsPageable;
import com.lpdm.msuser.proxy.ProductProxy;
import com.lpdm.msuser.services.shop.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ProductProxy productProxy;

    @Autowired
    public ProductServiceImpl(ProductProxy productProxy) {
        this.productProxy = productProxy;
    }

    @Override
    public List<Category> findAllCategories() {

        List<Category> categoryList = productProxy.findAllCategories();
        categoryList.forEach(c->
                c.setName(c.getName().substring(0,1).toUpperCase()
                + c.getName().substring(1)));

        return categoryList;
    }

    @Override
    public ProductsPageable findProductPageable(int page, int size) {

        ProductsPageable s = productProxy.getProductPageable(page, size);

        log.info("ProductPageable = " + s);

        return s;
    }

    @Override
    public ProductsPageable findProductPromotions(int page, int size) {

        return productProxy.getProductPageable(page, size);
    }

    @Override
    public List<Product> findProductsByCategory(int category) {

        List<Product> productList = productProxy.findProductsByCategory(category);
        log.info("product list : " + productList);

        return productList;
    }

    @Override
    public String getCategoryName(int category) {

        List<Category> categoryList = findAllCategories();
        Optional<Category> optCat = categoryList
                .stream()
                .filter(c -> category == c.getId())
                .findFirst();

        String selectedCategory = null;
        if(optCat.isPresent()) {
            selectedCategory = optCat.get().getName();
        }
        return selectedCategory;
    }

    @Override
    public Product findProductById(int id) {

        return productProxy.findProductById(id);
    }

    @Override
    public int coundProductQuatity(Product product) {

        int quantity = 0;

        for(Stock stock : product.getListStock()){

            quantity += stock.getQuantity();
        }
        return quantity;
    }
}
