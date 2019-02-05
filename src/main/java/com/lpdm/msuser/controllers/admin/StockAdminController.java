package com.lpdm.msuser.controllers.admin;

import com.lpdm.msuser.model.admin.SearchForm;
import com.lpdm.msuser.model.product.Stock;
import com.lpdm.msuser.services.admin.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Map;

import static com.lpdm.msuser.utils.admin.ValueType.*;

@RestController
@RequestMapping(STOCK_ADMIN_PATH)
public class StockAdminController {

    private final AdminService adminService;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StockAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = DEFAULT_PATH)
    public ModelAndView adminStock(){
        return new ModelAndView(STOCK_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STOCK_PAGE_TITLE);
    }

    @GetMapping(value = DEFAULT_SEARCH_PATH)
    public ModelAndView stockSearch(){
        return new ModelAndView(STOCK_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STOCK_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @PostMapping(value = DEFAULT_SEARCH_PATH)
    public ModelAndView stockSearchResult(
            @Valid
            @ModelAttribute(HTML_PAGE_SEARCH_FORM) SearchForm searchForm){

        String keyword = searchForm.getKeyword();
        int value = searchForm.getSearchValue();
        Object result = null;

        log.info("Key[" + value + "] : " + keyword);

        switch (value){
            case SEARCH_STOCK_BY_ID:
                result = adminService.findStockById(Integer.parseInt(keyword));
                break;
            case SEARCH_STOCK_BY_PRODUCT_ID:
                result = adminService.findStockByProductId(Integer.parseInt(keyword));
                break;
            case SEARCH_STOCK_BY_PRODUCT_NAME:
                result = adminService.findStockByProductName(keyword);
                break;
        }

        return new ModelAndView(STOCK_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STOCK_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm())
                .addObject(HTML_RESULT_OBJECT, result);
    }

    @GetMapping(value = {"/add", "/add/"})
    public ModelAndView addStock(){
        return new ModelAndView(STOCK_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STOCK_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, "addStock")
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @PostMapping(value = {"/delete", "/delete/"})
    public String deleteStock(@RequestParam Map<String, String> data){

        log.info("Stock id = " + data.get("stockId"));
        adminService.deleteStockById(Integer.parseInt(data.get("stockId")));

        return "ok";
    }

    @PostMapping(value = {"/update", "/update/"})
    public Stock updateStock(@RequestBody Stock stock){

        log.info("Stock Json = " + stock);

        return adminService.updateStock(stock);
    }

    @GetMapping(value = {"/search/product/{id}", "/search/product/{id}/"})
    public ModelAndView findStockById(@PathVariable int id){

        Object result = adminService.findStockByProductId(id);

        return new ModelAndView(STOCK_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STOCK_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm())
                .addObject(HTML_RESULT_OBJECT, result);
    }

    @PostMapping(value = {"/add/search", "/add/search/"})
    public ModelAndView stockAddSearchResult(@Valid @ModelAttribute("searchForm") SearchForm searchForm){

        String keyword = searchForm.getKeyword();
        int value = searchForm.getSearchValue();
        Object result = null;

        log.info("Key[" + value + "] : " + keyword);

        switch (value){
            case SEARCH_STOCK_BY_PRODUCT_ID:
                result = adminService.findStockByProductId(Integer.parseInt(keyword));
                break;
            case SEARCH_STOCK_BY_PRODUCT_NAME:
                result = adminService.findStockByProductName(keyword);
                break;
        }

        return new ModelAndView(STOCK_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, STOCK_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, "addStock")
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm())
                .addObject(HTML_RESULT_OBJECT, result);
    }

    @PostMapping(value = {"/add", "/add/"})
    public Stock addNewStock(@Valid @RequestBody Stock stock){

        log.info("Stock : " + stock.toString());
        return adminService.addNewStock(stock);
    }
}
