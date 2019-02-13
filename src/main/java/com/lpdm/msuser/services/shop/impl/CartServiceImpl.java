package com.lpdm.msuser.services.shop.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.model.shop.Cart;
import com.lpdm.msuser.model.shop.CookieProduct;
import com.lpdm.msuser.services.shop.CartService;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.services.shop.ProductService;
import com.lpdm.msuser.utils.cart.CartUtils;
import com.lpdm.msuser.utils.cookie.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lpdm.msuser.utils.shop.ValueType.COOKIE_CART;

@Service
public class CartServiceImpl implements CartService {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public CartServiceImpl(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @Override
    public Cart addProductToCart(CookieProduct cookieProduct,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws JsonProcessingException {

        log.info("Method : addProductToCart");
        log.info(" |_ Cookie product = " + cookieProduct);

        Product product = productService.findProductById(cookieProduct.getId());
        cookieProduct.setName(product.getName());
        cookieProduct.setPicture(product.getPicture());
        cookieProduct.setPrice(product.getPrice());
        cookieProduct.setTax(product.getTax());

        log.info(" |_ Cookie product now = " + cookieProduct);

        Cart cart = null;
        Cookie[] cookieList = request.getCookies();

        if(cookieList != null){
            for(Cookie cookie : cookieList){
                if(cookie.getName().equals(COOKIE_CART)){
                    log.info(" |_ Cookie temp_order exist");
                    try {
                        cart = CookieUtils.getCartFromCookie(cookie);

                        boolean found = cart
                                .getProductList()
                                .stream()
                                .anyMatch(p -> p.getId() == cookieProduct.getId());

                        if(found){
                            log.info(" |_ product found");

                            cart.getProductList()
                                    .stream()
                                    .filter(p-> p.getId() == cookieProduct.getId())
                                    .findFirst().ifPresent(p -> p.setQuantity(p.getQuantity() + cookieProduct.getQuantity()));
                        }
                        else {

                            log.info(" |_ product not found");
                            cart.getProductList().add(cookieProduct);
                        }

                        response.addCookie(CookieUtils
                                .getCookieFromCart(CartUtils
                                        .setAllCartInfos(cart)));
                    }
                    catch (IOException e) { log.warn(e.getMessage()); }
                }
            }
        }

        if(cart == null){

            log.info(" |_ Create new temp order");

            cart = new Cart();
            cart.getProductList().add(cookieProduct);

            log.info(" |_ Cart = " + cart);

            response.addCookie(CookieUtils
                    .getCookieFromCart(CartUtils
                            .setAllCartInfos(cart)));
        }

        return cart;
    }

    @Override
    public Cart getCartFormCookie(HttpServletRequest request) throws IOException {

        log.info("Method : getCartFormCookie");

        Cart cart = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {

            Cookie cookieCart = CookieUtils.isThereATempOrderFromCookies(cookies);

            if(cookieCart != null){

                log.info(" |_ Cart from cookie found");
                cart = CookieUtils.getCartFromCookie(cookieCart);
            }
        }
        else log.info(" |_ There are no cookies");

        return cart;
    }

    @Override
    public Cart updateQuantity(int productId, String mode,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {

        log.info("Method : updateQuantity");

        Cart cart = getCartFormCookie(request);

        for(CookieProduct product : cart.getProductList()){

            if(product.getId() == productId){

                switch (mode){

                    case "less" :
                        product.setQuantity(product.getQuantity() - 1);
                        log.info(" |_ -1");
                        break;

                    case "more" :
                        product.setQuantity(product.getQuantity() + 1);
                        log.info(" |_ +1");
                        break;
                }

                break;
            }
        }

        response.addCookie(CookieUtils.getCookieFromCart(CartUtils.setAllCartInfos(cart)));

        return cart;
    }

    @Override
    public Cart deleteProductFromCart(int productId,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {

        log.info("Method : deleteProductFromCart");

        Cart cart = getCartFormCookie(request);
        cart.getProductList().removeIf(p -> p.getId() == productId);

        response.addCookie(CookieUtils.getCookieFromCart(CartUtils.setAllCartInfos(cart)));

        return cart;
    }
}
