package org.ecommerce.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Entity
@Data
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.ORDINAL)
    private PaymentMethodType type;


    @Size(min = 10, message = "Length of account id cannot be less than 10")
    private String accountId;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Override
    public String toString() {
        return "Paid using - " +
                type +
                ", accountId='" + accountId.replaceAll(".(?=.{4})", "X") + '\''
                ;
    }
}
