package org.ecommerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecommerce.user.entities.Address;
import org.ecommerce.user.entities.PaymentMethod;
import org.ecommerce.user.entities.Role;

import java.util.List;


@Data
@AllArgsConstructor
public class UserResponseBody {

    private String email;
    private Role role;
    private String name;

    private int id;

    private List<Address> addresses;


    private String imgLink;
    private List<PaymentMethod> paymentMethods;
}
