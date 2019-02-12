package com.lpdm.msuser.services.shop.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.model.order.Status;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.services.shop.CartService;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.services.shop.ProductService;
import com.lpdm.msuser.utils.cookie.CookieUtils;
import com.lpdm.msuser.utils.order.OrderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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
    public Order addProductToCart(OrderedProduct orderedProduct,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws JsonProcessingException {

        Order order = null;
        Cookie[] cookieList = request.getCookies();

        log.info("Ordered product = " + orderedProduct);

        if(cookieList != null){
            for(Cookie cookie : cookieList){

                if(cookie.getName().equals("order")){

                    try {
                        order = CookieUtils.getOrderFromCookie(cookie);

                        List<OrderedProduct> orderedProductList = order.getOrderedProducts();

                        boolean found = orderedProductList
                                .stream()
                                .anyMatch(o -> o.getProductId() == orderedProduct.getProductId());

                        if(found){

                            log.info("product found !");
                            orderedProductList
                                    .stream()
                                    .filter(o-> o.getProductId() == orderedProduct.getProductId())
                                    .findFirst()
                                    .ifPresent(o -> o.setQuantity(o.getQuantity() + orderedProduct.getQuantity()));
                        }
                        else {

                            log.info("product not found !");
                            Product product = productService
                                    .findProductById(orderedProduct.getProductId());

                            orderedProduct.setProduct(product);
                            orderedProductList.add(orderedProduct);
                        }

                        for(OrderedProduct op : orderedProductList){
                            op.getProduct().setListStock(null);
                            op.getProduct().setProducer(null);
                        }

                        order.setOrderedProducts(orderedProductList);

                        response.addCookie(CookieUtils.getCookieFromOrder(order));

                    }
                    catch (IOException e) { log.warn(e.getMessage()); }
                }
            }
        }

        if(order == null){

            order = new Order();
            order.setStatus(Status.CART);
            order.getOrderedProducts().add(orderedProduct);

            for(OrderedProduct op : order.getOrderedProducts()){


                Product product = productService.findProductById(op.getProductId());
                product.setListStock(null);
                product.setProducer(null);
                op.setProduct(product);

                log.info("OrderedProduct now : " + op);
            }

            log.info("Order = " + order);

            response.addCookie(CookieUtils.getCookieFromOrder(order));
        }

        return order;
    }

    @Override
    public Order getCartFormCookie(HttpServletRequest request) throws IOException {

        Order order = null;
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {

            Cookie cookieOrder = CookieUtils.isThereAnOrderFromCookies(cookies);

            if(cookieOrder != null){

                log.info("Yes is there an Order from cookie");

                order = CookieUtils.getOrderFromCookie(cookieOrder);
            }
        }
        else log.info("No order from cookie");

        return order;
    }

    @Override
    public Order updateQuantity(int productId, String mode,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {

        Order order = getCartFormCookie(request);

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){

            if(orderedProduct.getProduct().getId() == productId){

                switch (mode){

                    case "less" :
                        orderedProduct.setQuantity(orderedProduct.getQuantity() - 1);
                        break;

                    case "more" :
                        orderedProduct.setQuantity(orderedProduct.getQuantity() + 1);
                        break;
                }

                orderedProduct.setTotalAmount(OrderUtils.getOrderedProductTotalAmount(orderedProduct));
                order.setTotal(OrderUtils.getTotalOrderAmount(order));

                response.addCookie(CookieUtils.getCookieFromOrder(order));

                return order;
            }
        }

        return null;
    }

    @Override
    public Order deleteProductFromCart(int productId,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {

        Order order = getCartFormCookie(request);

        order.getOrderedProducts().removeIf(o -> o.getProduct().getId() == productId);

        order.setTotal(OrderUtils.getTotalOrderAmount(order));

        response.addCookie(CookieUtils.getCookieFromOrder(order));

        return order;
    }
}
