package com.lpdm.msuser.services.admin;

import com.lpdm.msuser.model.auth.User;
import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.model.location.City;
import com.lpdm.msuser.model.order.Order;
import com.lpdm.msuser.model.product.Product;
import com.lpdm.msuser.model.product.Stock;
import com.lpdm.msuser.model.storage.Storage;
import com.lpdm.msuser.model.store.Store;
import com.lpdm.msuser.model.admin.OrderStats;
import com.lpdm.msuser.model.admin.SearchDates;
import com.lpdm.msuser.model.admin.StorageUser;
import com.lpdm.msuser.model.auth.UserRole;
import com.lpdm.msuser.model.order.Coupon;
import com.lpdm.msuser.model.order.Delivery;
import com.lpdm.msuser.model.order.Payment;
import com.lpdm.msuser.model.product.Category;
import com.netflix.discovery.shared.Application;
import feign.FeignException;

import java.util.List;
import java.util.Map;

public interface AdminService {

    /**
     * Order
     */
    Map<Order, List<String>> findOrderById(int id) throws FeignException;
    Map<Order, List<String>> findAllOrdersByUserId(int id);
    Map<Order, List<String>> findAllOrdersByUserEmail(String email);
    Map<Order, List<String>> findAllOrdersByUserLastName(String lastName);
    Map<Order, List<String>> findOrderByInvoiceReference(String ref);
    List<Payment> findAllPayment();
    OrderStats findOrderStatsByYear(Integer year);
    OrderStats getAverageStats(OrderStats stats1, OrderStats stats2);
    List<Order> findAllOrdersBetweenTwoDates(SearchDates dates);

    /**
     * Product
     */
    Product findProductById(int id);
    List<Category> findAllCategories();
    List<Product> findProductsByName(String name);
    List<Product> findProductsByProducerId(int id);
    OrderStats findOrderedProductsStatsByYear(int year);
    OrderStats findOrderedProductsStatsByYearAndCategory(int year);
    String getUploadPictureForm(StorageUser user);
    void updateProduct(Product product);
    Product addNewProduct(Product product);

    /**
     * Coupon
     */
    List<Coupon> findAllCoupons();
    Coupon addNewCoupon(Coupon coupon);
    Coupon updateCoupon(Coupon coupon);
    boolean deleteCoupon(Coupon coupon);

    /**
     * Delivery
     */
    List<Delivery> findAllDeliveryMethods();
    Delivery addNewDeliveryMethod(Delivery delivery);
    Delivery updateDeliveryMethod(Delivery delivery);
    boolean deleteDeliveryMethod(Delivery delivery);

    /**
     * Store
     */
    Store findStoreById(int id) throws FeignException;
    List<Store> findStoreByName(String name) throws FeignException;
    Store updateStore(Store store);
    Store addNewStore(Store store);

    /**
     * Eureka
     */
    List<Application> findAllApps() throws FeignException;
    void deleteInstance(String appId, String instanceId);

    /**
     * Storage
     */
    Storage findLatestFileUploadedByOwnerId(int id);

    /**
     * Stock
     */
    List<Product> findStockById(int id);
    List<Product> findStockByProductId(int id);
    List<Product> findStockByProductName(String name);
    void deleteStockById(int id);
    Stock updateStock(Stock stock);
    Stock addNewStock(Stock stock);

    /**
     * Auth
     */
    List<User> findUserById(int id);
    List<User> findUserByLastName(String lastName);
    List<UserRole> findAllUserRoles();
    User addNewUser(User user);
    List<User> findUserByEmail(String email);
    Integer getProducerRoleId();
    List<User> findUserByIdAndRole(int userId, int roleId);
    User updateUser(User user);

    /**
     * Location
     */

    Address findAddressById(int id);
    List<City> findCitiesByZipCode(String zipCode);
    Address saveNewAddress(Address address, int userId);
}
