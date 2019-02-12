package com.lpdm.msuser.controllers.shop;

import com.lpdm.msuser.model.product.Category;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.services.shop.CartService;
import com.lpdm.msuser.services.shop.ProductService;
import com.lpdm.msuser.utils.shop.CustomModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
public class ProductController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ProductService productService;
    private final CartService cartService;

    @Autowired
    public ProductController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping(value = "/shop/products")
    public ModelAndView productsPage(HttpServletRequest request) throws IOException {

        log.info("cook = " + request.getCookies());

        return CustomModel.getFor("shop/fragments/products/index", request, true)
                .addObject("productCategories", productService.findAllCategories())
                .addObject("promotions", productService.findProductPromotions(0,9));
    }

    @GetMapping(value = "/shop/products/cat/{id}")
    public ModelAndView productPageByCategory(@PathVariable int id,
                                              HttpServletRequest request)
            throws IOException {

        List<Category> categoryList = productService.findAllCategories();

        String selectedCategory = productService.getCategoryName(id);
        List<Product> productList = productService.findProductsByCategory(id);

        return CustomModel.getFor("shop/fragments/products/index", request, true)
                .addObject("productCategories", categoryList)
                .addObject("selectedCategory", selectedCategory)
                .addObject("productList", productList);
    }

    @GetMapping(value = "/shop/products/cat/{cat}/{productId}")
    public ModelAndView productPage(@PathVariable int cat,
                                    @PathVariable int productId,
                                    HttpServletRequest request) throws IOException {

        Product product = productService.findProductById(productId);
        int quantity = productService.coundProductQuatity(product);

        return CustomModel.getFor("shop/fragments/products/index", request, true)
                .addObject("productCategories", productService.findAllCategories())
                .addObject("selectedCategory", productService.getCategoryName(cat))
                .addObject("selectedProduct", product)
                .addObject("stockQuantity", quantity);
    }
}
