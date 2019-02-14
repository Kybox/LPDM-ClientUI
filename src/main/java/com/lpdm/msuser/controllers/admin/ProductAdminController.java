package com.lpdm.msuser.controllers.admin;

import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.model.storage.Storage;
import com.lpdm.msuser.model.admin.OrderStats;
import com.lpdm.msuser.model.admin.SearchForm;
import com.lpdm.msuser.model.admin.StorageUser;
import com.lpdm.msuser.services.admin.AdminService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.lpdm.msuser.utils.admin.ValueType.*;

@RestController
@RequestMapping(PRODUCT_ADMIN_PATH)
public class ProductAdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final AdminService adminService;

    @Autowired
    public ProductAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = DEFAULT_PATH)
    public ModelAndView adminProducts(){

        LocalDate date = LocalDate.now();
        OrderStats currentYear = adminService.findOrderedProductsStatsByYear(date.getYear());
        OrderStats lastYear = adminService.findOrderedProductsStatsByYear(date.getYear() - 1);
        OrderStats average = adminService.getAverageStats(currentYear, lastYear);

        OrderStats catCurrentYear = adminService.findOrderedProductsStatsByYearAndCategory(date.getYear());
        OrderStats catLastYear = adminService.findOrderedProductsStatsByYearAndCategory(date.getYear() - 1);
        OrderStats catAverage = adminService.getAverageStats(currentYear, lastYear);

        return new ModelAndView(PRODUCT_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, PRODUCT_PAGE_TITLE)
                .addObject("statsCurrentYear", currentYear)
                .addObject("statsLastYear", lastYear)
                .addObject("statsAverageYear", average)
                .addObject("statsCatCurrentYear", catCurrentYear)
                .addObject("statsCatLastYear", catLastYear)
                .addObject("statsCatAverageYear", catAverage)
                .addObject(HTML_PAGE_CONTENT, "stats");
    }

    @GetMapping(value = {"/search/{id}", "/search/{id}/"})
    public ModelAndView searchProductById(@PathVariable int id){

        Product product = adminService.findProductById(id);
        List<Product> result = new ArrayList<>();
        result.add(product);

        return new ModelAndView(PRODUCT_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, PRODUCT_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_RESULT_OBJECT, result)
                .addObject("categories", adminService.findAllCategories())
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @GetMapping(value = DEFAULT_SEARCH_PATH)
    public ModelAndView searchProduct(){
        return new ModelAndView(PRODUCT_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, PRODUCT_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @PostMapping(value = DEFAULT_SEARCH_PATH)
    public ModelAndView searchProductResult(
            @Valid @ModelAttribute("searchForm") SearchForm searchForm){

        log.info("Key[" + searchForm.getSearchValue() + "] : " + searchForm.getKeyword());
        String keyword = searchForm.getKeyword();
        Object result = null;
        try{
            switch (searchForm.getSearchValue()){
                case SEARCH_PRODUCT_BY_ID:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches()) {
                        result = new ArrayList<Product>();
                        ((ArrayList) result).add(adminService.findProductById(Integer.valueOf(keyword)));
                    }
                    else result = 500;
                    break;
                case SEARCH_PRODUCT_BY_NAME:
                    result = adminService.findProductsByName(keyword);
                    break;
                case SEARCH_PRODUCT_BY_PRODUCER_ID :
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches()) {
                        result = new ArrayList<Product>();
                        ((ArrayList) result).addAll(adminService.findProductsByProducerId(Integer.valueOf(keyword)));
                        if(((ArrayList) result).isEmpty()) {
                            result = 204;
                        }
                    }
                    else result = 500;
                    break;
            }
        }
        catch (FeignException e ){
            log.warn(e.getMessage());
            result = e.status();
        }

        return new ModelAndView(PRODUCT_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, PRODUCT_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_SEARCH_PAGE)
                .addObject(HTML_RESULT_OBJECT, result)
                .addObject("categories", adminService.findAllCategories())
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @PostMapping(value = {"/upload", "/upload/"},
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public String getUploadForm(@RequestParam Map<String, String> data){

        StorageUser user = new StorageUser();
        user.setId(Integer.parseInt(data.get("id")));
        user.setRestricted(Boolean.parseBoolean(data.get("restricted")));
        return adminService.getUploadPictureForm(user);

    }

    @GetMapping(value = {"/picture/owner/{id}", "/picture/owner/{id}/"})
    public Storage getLatestFileUploaded(@PathVariable int id){
        return adminService.findLatestFileUploadedByOwnerId(id);
    }

    @PostMapping(value = {"/update", "update/"})
    public Product updateProduct(@RequestBody Product productBean){

        log.info("Product update : " + productBean.toString());
        adminService.updateProduct(productBean);
        return productBean;
    }

    @GetMapping(value = {"/add", "/add/"})
    public ModelAndView addProduct(){
        return new ModelAndView(PRODUCT_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, PRODUCT_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_ADD_PAGE)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm());
    }

    @PostMapping(value = {"/add", "/add/"})
    public Product addNewProduct(@Valid @RequestBody Product product){

        log.info("Product : " + product);
        return adminService.addNewProduct(product);
    }

    @PostMapping(value = {"/search/producer", "/search/producer/"})
    public Object searchProducer(@Valid @ModelAttribute("searchProducer") SearchForm searchForm){

        Integer roleId = adminService.getProducerRoleId();


        log.info("Key[" + searchForm.getSearchValue() + "] : " + searchForm.getKeyword());
        String keyword = searchForm.getKeyword();
        int searchValue = searchForm.getSearchValue();
        Object result = null;

        try{
            switch (searchValue){
                // Search by id
                case 1:
                    if(Pattern.compile("^\\d+$").matcher(keyword).matches()) {
                        result = adminService.findUserByIdAndRole(Integer.parseInt(keyword), roleId);
                        if(((List) result).isEmpty()) result = 404;
                    }
                    else result = 500;
                    break;
                // Search by name
                case 2:
                    break;
            }
        }
        catch (FeignException e){
            log.info(e.getMessage());
            result = e.status();
        }

        return new ModelAndView(PRODUCT_FRAGMENT_PATH)
                .addObject(HTML_PAGE_TITLE, PRODUCT_PAGE_TITLE)
                .addObject(HTML_PAGE_CONTENT, HTML_DEFAULT_ADD_PAGE)
                .addObject(HTML_RESULT_OBJECT, result)
                .addObject(HTML_PAGE_SEARCH_FORM, new SearchForm())
                .addObject("categories", adminService.findAllCategories())
                .addObject("product", new Product());
    }
}
