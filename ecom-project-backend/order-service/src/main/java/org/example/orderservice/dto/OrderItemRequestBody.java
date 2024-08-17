package org.example.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class OrderItemRequestBody {
    @NotNull(message = "Product id cannot be empty")
    private String productId;

    @NotNull(message = "Size cannot be empty")
    private String size;

    @NotNull(message = "Quantity field cannot be empty")
    @Min(value = 1, message = "Quantity should be greater than 0")
    private int quantity;
}
