package org.example.orderservice.repository;

import org.example.orderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByUserId(int userId);

}
