package org.example.cartservice.dto;


import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartItemBodyDto {
    private String productId;

    private String size;

    @Min(value = 1, message = "Quantity should be greater than 0")
    private int quantity;
}
