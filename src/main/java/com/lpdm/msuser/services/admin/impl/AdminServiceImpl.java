package com.lpdm.msuser.services.admin.impl;

import com.lpdm.msuser.exception.EurekaInstanceNotFound;
import com.lpdm.msuser.model.admin.OrderStats;
import com.lpdm.msuser.model.admin.SearchDates;
import com.lpdm.msuser.model.admin.StorageUser;
import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.auth.UserRole;
import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.model.location.City;
import com.lpdm.msuser.model.order.*;
import com.lpdm.msuser.model.product.Category;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.model.product.Stock;
import com.lpdm.msuser.model.storage.Storage;
import com.lpdm.msuser.model.store.Store;
import com.lpdm.msuser.proxy.*;
import com.lpdm.msuser.services.admin.AdminService;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final StorageProxy storageProxy;
    private final OrderProxy orderProxy;
    private final ProductProxy productProxy;
    private final StoreProxy storeProxy;
    private final EurekaClient discoveryClient;
    private final StockProxy stockProxy;
    private final AuthProxy authProxy;
    private final LocationProxy locationProxy;

    @Autowired
    public AdminServiceImpl(OrderProxy orderProxy,
                            ProductProxy productProxy,
                            StoreProxy storeProxy,
                            StorageProxy storageProxy,
                            StockProxy stockProxy,
                            AuthProxy authProxy,
                            LocationProxy locationProxy,
                            @Qualifier("eurekaClient") EurekaClient discoveryClient) {

        this.orderProxy = orderProxy;
        this.productProxy = productProxy;
        this.storeProxy = storeProxy;
        this.discoveryClient = discoveryClient;
        this.storageProxy = storageProxy;
        this.stockProxy = stockProxy;
        this.authProxy = authProxy;
        this.locationProxy = locationProxy;
    }

    private List<String> calculateSubTotal(Order order){

        double subTotal = 0;
        double subTax = 0;

        List<String> resultList = new ArrayList<>();
        for(OrderedProduct orderedProduct : order.getOrderedProducts()){

            double productTotal = orderedProduct.getPrice() * orderedProduct.getQuantity();
            double taxTotal = productTotal * (orderedProduct.getTax() / 100);

            subTotal += productTotal;
            subTax += taxTotal;
        }

        subTotal = Math.round(subTotal * 100.0) / 100.0;
        subTax = Math.round(subTax * 100.0) / 100.0;

        double coupon = 0;
        if(order.getCoupon() != null) coupon = order.getCoupon().getAmount();

        double total = subTotal + subTax + coupon;

        DecimalFormat df = new DecimalFormat("0.00");

        resultList.add(df.format(subTotal));
        resultList.add(df.format(subTax));
        resultList.add(df.format(coupon));
        resultList.add(df.format(total));

        return resultList;
    }

    @Override
    public Map<Order, List<String>> findOrderById(int id) throws FeignException {

        Map<Order, List<String>> resultMap = new HashMap<>();

        Order order = orderProxy.getOrderById(id);
        resultMap.put(order, calculateSubTotal(order));

        return resultMap;
    }

    @Override
    public Map<Order, List<String>> findAllOrdersByUserId(int id) {

        List<Order> orderList = orderProxy.findAllByUserId(id);
        Map<Order, List<String>> resultMap = new HashMap<>();

        for(Order order : orderList) {
            order.setTotal(Math.round(order.getTotal() * 100D) / 100D);
            resultMap.put(order, calculateSubTotal(order));
        }

        return resultMap;
    }

    @Override
    public Map<Order, List<String>> findAllOrdersByUserEmail(String email) {

        List<Order> orderList = orderProxy.findAllByUserEmail(email);
        Map<Order, List<String>> resultMap = new HashMap<>();

        for(Order order : orderList)
            resultMap.put(order, calculateSubTotal(order));

        return resultMap;
    }

    @Override
    public Map<Order, List<String>> findAllOrdersByUserLastName(String lastName) {

        List<Order> orderList = orderProxy.findAllByUserLastName(lastName);
        Map<Order, List<String>> resultMap = new HashMap<>();

        for(Order order : orderList)
            resultMap.put(order, calculateSubTotal(order));

        return resultMap;
    }

    @Override
    public Map<Order, List<String>> findOrderByInvoiceReference(String ref) {

        Order order = orderProxy.findByInvoiceReference(ref);
        Map<Order, List<String>> resultMap = new HashMap<>();
        resultMap.put(order, calculateSubTotal(order));

        return resultMap;
    }

    @Override
    public List<Payment> findAllPayment() {
        return orderProxy.findAllPaymentMethods();
    }

    @Override
    public OrderStats findOrderStatsByYear(Integer year) {
        return orderProxy.findOrderStatsByYear(year);
    }

    @Override
    public OrderStats getAverageStats(OrderStats stats1, OrderStats stats2) {

        OrderStats averageStats = new OrderStats();

        for(Map.Entry<Object, Object> entry : stats1.getDataStats().entrySet()){

            Integer value1 = (Integer) entry.getValue();
            Integer value2 = (Integer) stats2.getDataStats().get(entry.getKey());
            double average = (value1.doubleValue() + value2.doubleValue()) / 2;

            averageStats.getDataStats().put(entry.getKey(), average);
        }
        return averageStats;
    }

    @Override
    public List<Order> findAllOrdersBetweenTwoDates(SearchDates dates) {
        log.info("Search dates : " + dates);
        return orderProxy.findAllOrdersBetweenDates(dates);
    }

    @Override
    public Product findProductById(int id) {
        return productProxy.findProduct(id);
    }

    @Override
    public List<Category> findAllCategories() {

        return productProxy.findAllCategories();
    }

    @Override
    public List<Product> findProductsByName(String name) {
        return productProxy.listProductByName(name);
    }

    @Override
    public List<Product> findProductsByProducerId(int id) {

        List<Product> productList = productProxy.listProductByProducerId(id);

        for(Product product : productList){

            User producer = product.getProducer();

            if (producer.getAddress() == null){
                Address address = locationProxy.findAddressById(producer.getAddressId());
                producer.setAddress(address);
            }
        }

        return productProxy.listProductByProducerId(id);
    }

    @Override
    public OrderStats findOrderedProductsStatsByYear(int year) {

        return orderProxy.findOrderedProductsStatsByYear(year);
    }

    @Override
    public OrderStats findOrderedProductsStatsByYearAndCategory(int year) {

        return orderProxy.findOrderedProductsStatsByYearAndCategory(year);
    }

    @Override
    public String getUploadPictureForm(StorageUser user) {

        return storageProxy.getUploadForm(user);
    }

    @Override
    public void updateProduct(Product product) {

        Product oldProduct = productProxy.findProduct(product.getId());

        if(product.getPicture() == null)
            product.setPicture(oldProduct.getPicture());

        product.setProducer(oldProduct.getProducer());

        log.info("New Product : " + product.toString());

        productProxy.updateProduct(product);
    }

    @Override
    public Product addNewProduct(Product product) {

        return productProxy.addProduct(product);
    }

    @Override
    public List<Coupon> findAllCoupons() {

        List<Coupon> couponList = null;

        try { couponList = orderProxy.getAllCoupons(); }
        catch (FeignException e){ log.warn(e.getMessage()); }

        return couponList;
    }

    @Override
    public Coupon addNewCoupon(Coupon coupon) {

        return orderProxy.addNewCoupon(coupon);
    }

    @Override
    public Coupon updateCoupon(Coupon coupon) {

        return orderProxy.updateCoupon(coupon);
    }

    @Override
    public boolean deleteCoupon(Coupon coupon) {

        return orderProxy.deleteCoupon(coupon);
    }

    @Override
    public List<Delivery> findAllDeliveryMethods() {

        List<Delivery> deliveryList = null;

        try { deliveryList = orderProxy.findAllDeliveryMethods(); }
        catch (FeignException e) { log.warn(e.getMessage()); }

        return deliveryList;
    }

    @Override
    public Delivery addNewDeliveryMethod(Delivery delivery) {

        return orderProxy.addNewDeliveryMethod(delivery);
    }

    @Override
    public Delivery updateDeliveryMethod(Delivery delivery) {

        return orderProxy.updateDeliveryMethod(delivery);
    }

    @Override
    public boolean deleteDeliveryMethod(Delivery delivery) {

        return orderProxy.deleteDeliveryMethod(delivery);
    }

    @Override
    public Store findStoreById(int id) throws FeignException {

        return storeProxy.findById(id);
    }

    @Override
    public List<Store> findStoreByName(String name) throws FeignException {
        return storeProxy.findByName(name);
    }

    @Override
    public Store updateStore(Store store) {

        Address address = locationProxy.saveNewAddress(store.getAddress());

        log.info("new address = " + address);

        store.setAddressId(address.getId());

        return storeProxy.updateStore(store);
    }

    @Override
    public Store addNewStore(Store store) {

        Address address = locationProxy.saveNewAddress(store.getAddress());

        store.setAddressId(address.getId());

        return storeProxy.addNewStore(store);
    }

    @Override
    public List<Application> findAllApps() throws FeignException {

        return discoveryClient.getApplications().getRegisteredApplications();
    }

    @Override
    public void deleteInstance(String appId, String instanceId) {

        Application app = discoveryClient.getApplication(appId);
        if(app == null) throw new EurekaInstanceNotFound();
        log.info("AppId : " + app.getName());
        log.info("Instance list : " );
        for(InstanceInfo inst : app.getInstances())
            log.info(" - " + inst.getId());

        InstanceInfo instance = app.getByInstanceId(instanceId);
        if(instance == null) throw new EurekaInstanceNotFound();
        log.info("InstanceId to remove : " + instance.getId());

        //instance.setIsDirty();

        log.info("Instance status before : " + instance.getStatus().toString());
        instance.setStatus(InstanceInfo.InstanceStatus.DOWN);
        log.info("Instance status after : " + instance.getStatus().toString());

        app.removeInstance(instance);
        log.info("Instance removed");
        for(InstanceInfo inst : app.getInstances())
            log.info(" - " + inst.getId());

        instance.setOverriddenStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);
        app.removeInstance(instance);
        log.info("Instance removed");
        for(InstanceInfo inst : app.getInstances())
            log.info(" - " + inst.getId());

        instance.setStatusWithoutDirty(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);
        app.removeInstance(instance);
        log.info("Instance removed");
        for(InstanceInfo inst : app.getInstancesAsIsFromEureka())
            log.info(" - " + inst.getId());



        discoveryClient.getApplicationInfoManager().refreshDataCenterInfoIfRequired();


    }

    @Override
    public Storage findLatestFileUploadedByOwnerId(int id) {
        return storageProxy.getLatestFileUploadByOwner(id);
    }

    @Override
    public List<Product> findStockById(int id) throws FeignException {

        Stock stock = stockProxy.findStockById(id);
        Product product = productProxy.findProduct(stock.getProductId());
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        return productList;
    }

    @Override
    public List<Product> findStockByProductId(int id) {
        Product product = productProxy.findProduct(id);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        return productList;
    }

    @Override
    public List<Product> findStockByProductName(String name) {

        return productProxy.listProductByName(name);
    }

    @Override
    public void deleteStockById(int id) {

        stockProxy.deleteStockById(id);
    }

    @Override
    public Stock updateStock(Stock stock) {
        return stockProxy.updateStock(stock);
    }

    @Override
    public Stock addNewStock(Stock stock) {
        return stockProxy.addNewStock(stock);
    }

    @Override
    public List<User> findUserById(int id) {

        User user = authProxy.findById(id);
        List<User> userList = new ArrayList<>();
        userList.add(user);

        return userList;
    }

    @Override
    public List<User> findUserByLastName(String lastName) {
        return authProxy.findByLastName(lastName);
    }

    @Override
    public List<UserRole> findAllUserRoles() {
        return authProxy.findAllRoles();
    }

    @Override
    public User addNewUser(User user) {
        return authProxy.addNewUser(user);
    }

    @Override
    public List<User> findUserByEmail(String email) {

        List<User> userList = new ArrayList<>();
        userList.add(authProxy.findByEmail(email));
        return userList;
    }

    @Override
    public Integer getProducerRoleId() {

        List<UserRole> roleList = authProxy.findAllRoles();
        for(UserRole role : roleList){
            if(role.getRoleName().toLowerCase().equals("producer")){
                return role.getId();
            }
        }

        return null;
    }

    @Override
    public List<User> findUserByIdAndRole(int userId, int roleId) {

        User user = authProxy.findUserByIdAndRole(userId, roleId);
        List<User> userList = new ArrayList<>();
        if(user != null) userList.add(user);
        return userList;
    }

    @Override
    public User updateUser(User user) {
        return authProxy.updateUser(user);
    }

    @Override
    public Address findAddressById(int id) {
        return locationProxy.findAddressById(id);
    }

    @Override
    public List<City> findCitiesByZipCode(String zipCode) {
        return locationProxy.findCitiesByZipCode(zipCode);
    }

    @Override
    public Address saveNewAddress(Address address, int userId) {

        address = locationProxy.saveNewAddress(address);

        User user = authProxy.findById(userId);
        user.setAddressId(address.getId());

        authProxy.updateUser(user);
        return address;
    }
}
