package org.example.productservice.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.productservice.models.Product;

import java.util.List;


@Data
public class ProductUpdateBody {

    @Size(min = 3, message = "Name should be at least 3 characters")
    private String name;

    private String description;

    private List<Product.Attribute> attributes;

    @Positive(message = "Price should be greater than 0")
    private Double price;

    private String brand;

    private String quantity;

    private List<String> imgLinks;


}
