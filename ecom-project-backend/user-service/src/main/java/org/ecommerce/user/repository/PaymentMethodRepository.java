package org.ecommerce.user.repository;

import org.ecommerce.user.entities.PaymentMethod;
import org.ecommerce.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Integer> {

    boolean existsByUserAndAccountId(User user, String accountId);
}
