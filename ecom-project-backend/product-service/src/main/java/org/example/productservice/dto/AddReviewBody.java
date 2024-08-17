package org.example.productservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddReviewBody {

    private String description;

    @NotNull(message = "rating cannot be empty")
    private double rating;

}
