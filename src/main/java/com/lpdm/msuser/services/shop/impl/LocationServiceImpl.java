package com.lpdm.msuser.services.shop.impl;

import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.model.location.City;
import com.lpdm.msuser.proxy.LocationProxy;
import com.lpdm.msuser.services.shop.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationProxy locationProxy;

    @Override
    public List<City> findCitiesByZipCode(String zipCode) {

        return locationProxy.findCitiesByZipCode(zipCode);
    }

    @Override
    public Address addNewAddress(Address address) {

        return locationProxy.saveNewAddress(address);
    }

    @Override
    public Address findAddressById(int id) {

        return locationProxy.findAddressById(id);
    }

    @Override
    public Address updateAddress(Address address) {

        return locationProxy.updateAddress(address);
    }
}
