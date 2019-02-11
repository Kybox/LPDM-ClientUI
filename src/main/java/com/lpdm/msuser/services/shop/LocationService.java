package com.lpdm.msuser.services.shop;

import com.lpdm.msuser.model.location.Address;
import com.lpdm.msuser.model.location.City;

import java.util.List;

public interface LocationService {

    List<City> findCitiesByZipCode(String zipCode);

    Address addNewAddress(Address address);

    Address findAddressById(int id);

    Address updateAddress(Address address);
}
