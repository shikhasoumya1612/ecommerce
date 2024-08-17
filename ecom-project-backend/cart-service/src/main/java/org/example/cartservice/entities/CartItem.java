package org.example.cartservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Product field cannot be null")
    private String productId;

    @NotNull(message = "Size field cannot be null")
    private String size;

    @NotNull(message = "Quantity field cannot be null")
    @Min(value = 1, message = "Quantity should not be less than 1")
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotNull(message="Cart field cannot be null")
    private Cart cart;

}
