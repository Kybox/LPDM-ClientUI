package com.lpdm.msuser.services.shop;

import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.order.OrderedProduct;

public interface OrderService {

    int getTotalOrderedProducts(Order order);

    double getOrderedProductTotalAmount(OrderedProduct orderedProduct);

    double getOrderedProductPriceWithTax(OrderedProduct orderedProduct);

    double getTotalOrderAmount(Order order);
}
