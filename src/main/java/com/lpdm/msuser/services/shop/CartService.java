package com.lpdm.msuser.services.shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.shop.Cart;
import com.lpdm.msuser.model.shop.CookieProduct;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface CartService {

    Cart addProductToCart(CookieProduct cookieProduct,
                          HttpServletRequest request,
                          HttpServletResponse response) throws JsonProcessingException;

    Cart getCartFormCookie(HttpServletRequest request) throws IOException;

    Cart updateQuantity(int productId, String mode,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException;

    Cart deleteProductFromCart(int productId,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException;

}
