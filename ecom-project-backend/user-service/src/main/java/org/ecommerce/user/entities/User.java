package org.ecommerce.user.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(min = 3, message = "Name should have least 3 characters")
    private String name;

    @NotBlank(message = "Password field should not empty")
    private String password;

    @Email(message = "Invalid email format : Please provide a valid email address.")
    @Column(unique = true)
    @NotBlank(message = "Email field should not be empty")
    private String email;


    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PaymentMethod> paymentMethods;


    private String imgLink;

    public User() {
        // no args constructor
    }

}
