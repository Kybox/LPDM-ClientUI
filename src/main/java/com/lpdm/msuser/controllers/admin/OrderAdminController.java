package com.lpdm.msuser.controllers.admin;

import com.lpdm.msuser.model.admin.OrderStats;
import com.lpdm.msuser.model.admin.SearchForm;
import com.lpdm.msuser.services.admin.AdminService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/admin/orders")
public class OrderAdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final AdminService adminService;

    @Autowired
    public OrderAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = {"", "/"})
    public ModelAndView adminOrders(){

        LocalDate date = LocalDate.now();
        OrderStats currentYear = adminService.findOrderStatsByYear(date.getYear());
        OrderStats lastYear = adminService.findOrderStatsByYear(date.getYear() - 1);
        OrderStats average = adminService.getAverageStats(currentYear, lastYear);

        return new ModelAndView("admin/fragments/orders")
                .addObject("pageTitle","Admin orders")
                .addObject("statsCurrentYear", currentYear)
                .addObject("statsLastYear", lastYear)
                .addObject("statsAverageYear", average);
    }

    @GetMapping(value = {"/search", "/search/"})
    public ModelAndView searchOrder(){
        return new ModelAndView("/admin/fragments/orders")
                .addObject("pageTitle", "Search order")
                .addObject("content", "searchPage")
                .addObject("searchForm", new SearchForm())
                .addObject("payments", adminService.findAllPayment())
                .addObject("selectedTab", "order_id");
    }

    @PostMapping(value = {"/search", "/search/"})
    public ModelAndView searchOrderResult(
            @Valid @ModelAttribute("searchForm") SearchForm searchForm){

        log.info("SearchForm = " + searchForm);
        log.info("Keyword = " + searchForm.getKeyword());
        log.info("Value = " + searchForm.getSearchValue());

        String keyword = searchForm.getKeyword();
        String selectedTab = null;
        Object result = null;
        try{
            switch (searchForm.getSearchValue()){
                // Search by order id
                case 1:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches())
                        result = adminService.findOrderById(Integer.valueOf(keyword));
                    else result = 500;
                    selectedTab = "order_id";
                    break;
                // Search by user id
                case 2:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches())
                        result = adminService.findAllOrdersByUserId(Integer.valueOf(keyword));
                    else result = 500;
                    selectedTab = "customer";
                    break;
                // Search by user email
                case 3:
                    result = adminService.findAllOrdersByUserEmail(keyword);
                    selectedTab = "customer";
                    break;
                    /*
                case 2:
                    result = adminService.findOrderByInvoiceReference(keyword);
                    break;
                case 3:
                    result = adminService.findAllOrdersByUserEmail(keyword);
                    break;
                case 4:
                    result = adminService.findAllOrdersByUserLastName(keyword);
                    */
            }
        }
        catch (FeignException e ){
            log.warn(e.getMessage());
            result = e.status();
        }

        return new ModelAndView("/admin/fragments/orders")
                .addObject("pageTitle", "Search order")
                .addObject("content", "searchPage")
                .addObject("result", result)
                .addObject("searchForm", new SearchForm())
                .addObject("payments", adminService.findAllPayment())
                .addObject("selectedTab", selectedTab);
    }

    @GetMapping(value = {"/payments", "/payments/"})
    public ModelAndView getAllPayments(){

        return new ModelAndView("/admin/fragments/orders")
                .addObject("pageTitle", "Search order")
                .addObject("content", "paymentsPage")
                .addObject("result", adminService.findAllPayment());
    }
}
