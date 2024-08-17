package org.ecommerce.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(min = 2, message = "Address name should have least 2 characters")
    private String addressName;
    private String apartment;

    private String area;

    private String landmark;

    @Size(min = 6, message = "Pin code should be of 6 digits")
    @NotBlank(message = "Pincode cannot be empty")
    private String pincode;

    private String city;
    private String state;

    @ManyToOne
    @JsonIgnore
    private User user;


    @Override
    public String toString() {
        return "Address - " +
                addressName + ", " +
                apartment + ", " +
                area + ", " +
                landmark + ", " +
                pincode + ", " +
                city + ", " +
                state
                ;
    }
}
