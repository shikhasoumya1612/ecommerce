package org.ecommerce.user.repository;

import org.ecommerce.user.entities.Address;
import org.ecommerce.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Integer> {

    boolean existsByUserAndAddressName(User user, String addressName);
}
