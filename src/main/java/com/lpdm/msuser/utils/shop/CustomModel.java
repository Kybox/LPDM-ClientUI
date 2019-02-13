package com.lpdm.msuser.utils.shop;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;
import com.lpdm.msuser.model.order.Status;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.model.shop.Cart;
import com.lpdm.msuser.model.shop.CookieProduct;
import com.lpdm.msuser.services.shop.CartService;
import com.lpdm.msuser.services.shop.OrderService;
import com.lpdm.msuser.services.shop.ProductService;
import com.lpdm.msuser.services.shop.SecurityService;
import com.lpdm.msuser.utils.order.OrderUtils;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CustomModel {

    private static Logger log = LoggerFactory.getLogger(CustomModel.class);

    private static CartService cartService;
    private static OrderService orderService;
    private static SecurityService securityService;
    private static ProductService productService;

    @Autowired
    public CustomModel(CartService cartService,
                       OrderService orderService,
                       SecurityService securityService,
                       ProductService productService) {

        CustomModel.cartService = cartService;
        CustomModel.orderService = orderService;
        CustomModel.securityService = securityService;
        CustomModel.productService = productService;
    }

    public static ModelAndView getFor(String url,
                                      HttpServletRequest request,
                                      boolean getCartFromCookie)
            throws IOException {

        log.info("Method : getFor");

        Order order;
        Cart cart = cartService.getCartFormCookie(request);

        if(!getCartFromCookie) {
            try{
                User user = securityService.getAuthenticatedUser(request);
                order = orderService.findLastOrderByCustomerAndStatus(user.getId(), Status.VALIDATED.getId());
                log.info(" |_ Last order validated = " + order);

                boolean userAddress = user.getAddress() != null;

                return new ModelAndView(url)
                        .addObject("cookieOrder", order)
                        .addObject("totalProducts", OrderUtils.getTotalOrderedProducts(order))
                        .addObject("subAmount", OrderUtils.getTotalOrderAmountWithoutTax(order))
                        .addObject("user", user)
                        .addObject("userAddress", userAddress);
            }
            catch (FeignException e) {
                log.info(e.getMessage());
            }
        }

        if(cart != null && cart.getProductList() != null && cart.getProductList().size() != 0){

            order = new Order();
            for(CookieProduct cookieProduct : cart.getProductList()){

                Product product = productService.findProductById(cookieProduct.getId());
                product.setProducer(null);
                product.setListStock(null);

                OrderedProduct orderedProduct = new OrderedProduct();
                orderedProduct.setProduct(product);
                orderedProduct.setQuantity(cookieProduct.getQuantity());

                order.getOrderedProducts().add(orderedProduct);
            }

            ModelAndView modelAndView =  new ModelAndView(url)
                    .addObject("cookieOrder", OrderUtils.setAllAmountsFromOrder(order))
                    .addObject("totalProducts", OrderUtils.getTotalOrderedProducts(order))
                    .addObject("subAmount", OrderUtils.getTotalOrderAmountWithoutTax(order));

            User user = securityService.getAuthenticatedUser(request);

            if(user != null)
                modelAndView.addObject("user", user);

            return modelAndView;
        }

        log.info(" |_ No cart found in the cookies");

        return new ModelAndView(url);
    }
}
