package org.example.cartservice.repository;

import org.example.cartservice.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.productId = :productId AND ci.size = :size")
    Optional<CartItem> findByCartAndProductAndSize(@Param("cartId") int cartId, @Param("productId") String productId, @Param("size") String size);


}
