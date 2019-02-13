package com.lpdm.msuser.utils.cart;

import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.model.shop.Cart;
import com.lpdm.msuser.model.shop.CookieProduct;
import com.lpdm.msuser.utils.order.OrderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CartUtils {

    private static Logger log = LoggerFactory.getLogger(CartUtils.class);

    public static Cart setAllCartInfos(Cart cart){

        log.info("Method : setAllCartInfos");

        int totalProducts = 0;
        double amountWithoutTax = 0;
        double amountWithTax = 0;

        for(CookieProduct cookieProduct : cart.getProductList()){

            double amount = cookieProduct.getPrice() * cookieProduct.getQuantity();
            double priceWithTax = amount + (amount * (cookieProduct.getTax() / 100));

            cookieProduct.setPriceWithTax(Math.round(priceWithTax * 100D) / 100D);

            amountWithoutTax += amount;
            totalProducts += cookieProduct.getQuantity();
            amountWithTax += priceWithTax;
        }

        cart.setTotalProducts(totalProducts);
        cart.setAmountWithTax(Math.round(amountWithTax * 100D) / 100D);
        cart.setAmountWithoutTax(Math.round(amountWithoutTax * 100D) / 100D);

        log.info(" |_ Total products = " + cart.getTotalProducts());
        log.info(" |_ Amount with tax = " + cart.getAmountWithTax());
        log.info(" |_ Amount without tax = " + cart.getAmountWithoutTax());

        return cart;
    }

    public static Cart getCartFromOrder(Order order){

        Cart cart = new Cart();
        cart.setId(order.getId());

        for(OrderedProduct orderedProduct : order.getOrderedProducts()){

            CookieProduct cookieProduct = new CookieProduct();
            cookieProduct.setId(orderedProduct.getProduct().getId());
            cookieProduct.setName(orderedProduct.getProduct().getName());
            cookieProduct.setPicture(orderedProduct.getProduct().getPicture());
            cookieProduct.setQuantity(orderedProduct.getQuantity());
            cookieProduct.setPrice(orderedProduct.getPrice());
            cookieProduct.setTax(orderedProduct.getTax());
            cookieProduct.setPriceWithTax(OrderUtils.getOrderedProductPriceWithTax(orderedProduct));

            cart.getProductList().add(cookieProduct);
        }

        return CartUtils.setAllCartInfos(cart);
    }
}
