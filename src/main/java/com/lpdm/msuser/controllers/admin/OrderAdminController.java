package com.lpdm.msuser.controllers.admin;

import com.lpdm.msuser.model.admin.OrderStats;
import com.lpdm.msuser.model.admin.SearchDates;
import com.lpdm.msuser.model.admin.SearchForm;
import com.lpdm.msuser.model.order.Coupon;
import com.lpdm.msuser.model.order.Delivery;
import com.lpdm.msuser.model.order.Status;
import com.lpdm.msuser.services.admin.AdminService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static com.lpdm.msuser.utils.admin.ValueType.*;

@RestController
@RequestMapping(ORDER_ADMIN_PATH)
public class OrderAdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final AdminService adminService;

    @Autowired
    public OrderAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = DEFAULT_PATH)
    public ModelAndView adminOrders(){

        LocalDate date = LocalDate.now();
        OrderStats currentYear = adminService.findOrderStatsByYear(date.getYear());
        OrderStats lastYear = adminService.findOrderStatsByYear(date.getYear() - 1);
        OrderStats average = adminService.getAverageStats(currentYear, lastYear);

        return new ModelAndView(ORDER_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE,ORDER_PAGE_TITLE)
                .addObject("statsCurrentYear", currentYear)
                .addObject("statsLastYear", lastYear)
                .addObject("statsAverageYear", average);
    }

    @GetMapping(value = DEFAULT_SEARCH_PATH)
    public ModelAndView searchOrder(){
        return new ModelAndView(ORDER_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, ORDER_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm())
                .addObject("payments", adminService.findAllPayment())
                .addObject("selectedTab", "order_id")
                .addObject("statusList", Status.values());
    }

    @PostMapping(value = DEFAULT_SEARCH_PATH)
    public ModelAndView searchOrderResult(
            @Valid @ModelAttribute("searchForm") SearchForm searchForm){

        log.info("SearchForm = " + searchForm);
        log.info("Keyword = " + searchForm.getKeyword());
        log.info("Value = " + searchForm.getSearchValue());

        int searchValue = searchForm.getSearchValue();
        String keyword = searchForm.getKeyword();
        String selectedTab = null;
        Object result = null;

        try{
            switch (searchValue){

                case SEARCH_ORDER_BY_ID:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches())
                        result = adminService.findOrderById(Integer.valueOf(keyword));
                    else result = 500;
                    selectedTab = "order_id";
                    break;

                case SEARCH_ORDER_BY_USER_ID:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches())
                        result = adminService.findAllOrdersByUserId(Integer.valueOf(keyword));
                    else result = 500;
                    selectedTab = "customer";
                    break;

                case SEARCH_ORDER_BY_USER_EMAIL:
                    result = adminService.findAllOrdersByUserEmail(keyword);
                    selectedTab = "customer";
                    break;

                case SEARCH_ORDER_BY_USER_NAME:
                    result = adminService.findAllOrdersByUserLastName(keyword);
                    selectedTab = "customer";
                    break;

                case SEARCH_ORDER_BY_INVOICE_REF:
                    result = adminService.findOrderByInvoiceReference(keyword);
                    selectedTab = "invoice";
                    break;

                case SEARCH_ORDER_BY_DATE:
                    String date1 = keyword.substring(0, keyword.lastIndexOf(":"));
                    String date2 = keyword.substring(keyword.lastIndexOf(":") + 1);
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    SearchDates dates = new SearchDates(
                            LocalDate.parse(date1, df),
                            LocalDate.parse(date2, df));
                    result = adminService.findAllOrdersBetweenTwoDates(dates);
                    selectedTab = "date";
                    break;
            }
        }
        catch (FeignException e ){
            log.warn(e.getMessage());
            result = e.status();
        }

        return new ModelAndView(ORDER_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, ORDER_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_RESULT_OBJECT, result)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm())
                .addObject("payments", adminService.findAllPayment())
                .addObject("selectedTab", selectedTab)
                .addObject("statusList", Status.values());
    }

    @GetMapping(value = {"/payments", "/payments/"})
    public ModelAndView getAllPayments(){

        return new ModelAndView(ORDER_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, ORDER_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, "paymentsPage")
                .addObject(HTML_RESULT_OBJECT, adminService.findAllPayment());
    }

    @GetMapping(value = {"/coupon", "/coupon/"})
    public ModelAndView getAllCoupons(){

        return new ModelAndView(ORDER_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, ORDER_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, "couponPage")
                .addObject("couponList", adminService.findAllCoupons())
                .addObject("coupon", new Coupon());
    }

    @PostMapping(value = {"/coupon", "/coupon/"})
    public Coupon addNewCoupon(@Valid @RequestBody Coupon coupon){

        log.info("Add coupon : " + coupon);

        return adminService.addNewCoupon(coupon);
    }

    @PutMapping(value = {"/coupon", "/coupon/"})
    public Coupon updateCoupon(@Valid @RequestBody Coupon coupon){

        log.info("Update coupon : " + coupon);

        return adminService.updateCoupon(coupon);
    }

    @DeleteMapping(value = {"/coupon", "/coupon/"})
    public boolean deleteCoupon(@Valid @RequestBody Coupon coupon){

        log.info("Delete coupon : " + coupon);

        return adminService.deleteCoupon(coupon);
    }

    @GetMapping(value = {"/delivery", "/delivery/"})
    public ModelAndView getAllDeliveryMethods(){

        return new ModelAndView(ORDER_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, ORDER_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, "deliveryPage")
                .addObject("deliveryList", adminService.findAllDeliveryMethods());
    }

    @PostMapping(value = {"/delivery", "/delivery/"})
    public Delivery addNewDeliveryMethod(@Valid @RequestBody Delivery delivery) {

        log.info("Add new delivery : " + delivery);

        return adminService.addNewDeliveryMethod(delivery);
    }

    @PutMapping(value = {"/delivery", "/delivery/"})
    public Delivery updateDeliveryMethod(@Valid @RequestBody Delivery delivery) {

        log.info("Update delivery : " + delivery);

        return adminService.updateDeliveryMethod(delivery);
    }

    @DeleteMapping(value = {"/delivery", "/delivery/"})
    public boolean deleteDeliveryMethod(@Valid @RequestBody Delivery delivery) {

        log.info("Delete delivery : " + delivery);

        return adminService.deleteDeliveryMethod(delivery);
    }
}
