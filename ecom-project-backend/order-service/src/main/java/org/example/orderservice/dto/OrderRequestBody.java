package org.example.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestBody {
    private List<OrderItemRequestBody> orderItemList;
    @NotNull(message = "Address id cannot be null")
    private int addressId;

    @NotNull(message = "Payment method id cannot be null")
    private int paymentMethodId;
}
