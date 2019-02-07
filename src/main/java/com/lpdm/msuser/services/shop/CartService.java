package com.lpdm.msuser.services.shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface CartService {

    Order addProductToCart(OrderedProduct orderedProduct,
                           HttpServletRequest request,
                           HttpServletResponse response) throws JsonProcessingException;

    Order getCartFormCookie(HttpServletRequest request) throws IOException;

    Order updateQuantity(int productId, String mode,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException;

    Order deleteProductFromCart(int productId,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException;

}
