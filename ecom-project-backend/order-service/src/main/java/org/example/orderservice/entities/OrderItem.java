package org.example.orderservice.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Product field cannot be null")
    private String productId;

    @NotNull(message = "Product name cannot be null")
    private String productName;

    @NotNull(message = "Product category cannot be null")
    private String category;

    @NotNull(message = "Product image cannot be null")
    private String img;

    @NotNull(message = "Price field cannot be empty")
    @Positive(message = "Price should be greater than 0")
    private double price;

    @NotNull(message = "Size cannot be empty")
    private String size;

    @NotNull(message = "Quantity field cannot be empty")
    @Min(value = 1, message = "Quantity should be greater than 0")
    private int quantity;

    @ManyToOne
    @JsonIgnore
    private Order order;
}
