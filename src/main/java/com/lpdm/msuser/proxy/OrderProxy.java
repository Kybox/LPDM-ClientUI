package com.lpdm.msuser.proxy;

import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfStream;
import com.lpdm.msuser.model.admin.OrderStats;
import com.lpdm.msuser.model.admin.SearchDates;
import com.lpdm.msuser.model.order.*;
import com.lpdm.msuser.model.paypal.TransactionInfo;
import feign.FeignException;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Component
@FeignClient(name = "zuul-server", url = "https://zuul.lpdm.kybox.fr")
@RibbonClient(name = "${lpdm.order.name}")
public interface OrderProxy {

    @GetMapping(value = "/ms-order/orders/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Order getOrderById(@PathVariable("id") int id) throws FeignException;

    @PostMapping(value = "/ms-order/orders/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Order saveOrder(@Valid @RequestBody Order order);

    // Find all orders by customer id
    @GetMapping(value = "${lpdm.order.name}/orders/all/customer/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Order> findAllByUserId(@PathVariable("id") int id);

    // Find all orders by customer id and status id
    @GetMapping(value = "${lpdm.order.name}/orders/all/customer/{customer}/status/{status}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Order> findAllByCustomerAndStatus(@PathVariable("customer") int customer,
                                           @PathVariable("status") int status);

    // Find last order by customer and status
    @GetMapping(value = "${lpdm.order.name}/orders/last/customer/{customer}/status/{status}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Order findLastOrderByCustomerAndStatus(@PathVariable("customer") int customer,
                                           @PathVariable("status") int status);

    // Find all orders by customer sorted by date
    @GetMapping(value = "${lpdm.order.name}/orders/all/customer/{id}/date/{sort}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Order> findAllByCustomerSortedByDate(@PathVariable("id") int id,
                                              @PathVariable("sort") String sort);

    // Find all payments
    @GetMapping(value = "${lpdm.order.name}/orders/payments",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Payment> findAllPaymentMethods();

    // Search orders by customer email
    @GetMapping(value = "${lpdm.order.name}/admin/orders/all/customer/email/{email}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Order> findAllByUserEmail(@PathVariable("email") String email);

    // Search orders by customer name
    @GetMapping(value = "${lpdm.order.name}/admin/orders/all/customer/name/{name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Order> findAllByUserLastName(@PathVariable("name") String name);

    @GetMapping(value = "${lpdm.order.name}/admin/orders/invoice/{ref}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Order findByInvoiceReference(@PathVariable("ref") String ref);

    // Search orders between 2 dates
    @PostMapping(value = "${lpdm.order.name}/admin/orders/dates/between",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Order> findAllOrdersBetweenDates(@Valid @RequestBody SearchDates searchDates);

    // Orders statistics by year
    @GetMapping(value = "${lpdm.order.name}/admin/orders/stats/year/{year}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    OrderStats findOrderStatsByYear(@PathVariable("year") Integer year);

    // Ordered products statistics by year
    @GetMapping(value = "${lpdm.order.name}/admin/orderedproducts/stats/year/{year}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    OrderStats findOrderedProductsStatsByYear(@PathVariable("year") Integer year);

    // Ordered products statistics by year and category
    @GetMapping(value = "${lpdm.order.name}/admin/orderedproducts/stats/year/{year}/category",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    OrderStats findOrderedProductsStatsByYearAndCategory(@PathVariable("year") Integer year);

    // Pay an order
    @PostMapping(value = "${lpdm.order.name}/orders/{id}/pay",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    PaypalUrl getPayPalUrl(@PathVariable("id") int id, @RequestBody SuccessUrl successUrl);

    // Get transaction details
    @PostMapping(value = "${lpdm.order.name}/orders/transaction/details")
    String getTransactionDetails(@RequestBody TransactionInfo transactionInfo);

    // Get all coupons
    @GetMapping(value = "${lpdm.order.name}/admin/coupon/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Coupon> getAllCoupons();

    // Add new coupon
    @PostMapping(value = "${lpdm.order.name}/admin/coupon/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Coupon addNewCoupon(@RequestBody Coupon coupon);

    @PutMapping(value = "${lpdm.order.name}/admin/coupon/update",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Coupon updateCoupon(@RequestBody Coupon coupon);

    // Delete a coupon
    @DeleteMapping(value = "${lpdm.order.name}/admin/coupon/delete",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    boolean deleteCoupon(@RequestBody Coupon coupon);

    // Check a coupon
    @GetMapping(value = "${lpdm.order.name}/orders/coupon/check")
    Coupon checkCoupon(@RequestParam String code);

    // Get all delivery methods
    @GetMapping(value = "${lpdm.order.name}/orders/delivery/all",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<Delivery> findAllDeliveryMethods();

    // Add new delivery method
    @PostMapping(value = "${lpdm.order.name}/admin/delivery/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Delivery addNewDeliveryMethod(@RequestBody Delivery delivery);

    // Update a delivery method
    @PutMapping(value = "${lpdm.order.name}/admin/delivery/update",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Delivery updateDeliveryMethod(@RequestBody Delivery delivery);

    // Delete a delivery method
    @DeleteMapping(value = "${lpdm.order.name}/admin/delivery/delete",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    boolean deleteDeliveryMethod(@RequestBody Delivery delivery);

    @GetMapping(value = "${lpdm.order.name}/{id}/invoice",
            produces = MediaType.APPLICATION_PDF_VALUE)
    ByteArrayOutputStream downloadInvoice(@PathVariable int id);
}
