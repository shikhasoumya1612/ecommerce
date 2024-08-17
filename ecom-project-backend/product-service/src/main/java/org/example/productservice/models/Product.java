package org.example.productservice.models;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;


@Document(collection = "products")
@Accessors(chain = true)
@NoArgsConstructor
@Data
@AllArgsConstructor



public class Product {

    @MongoId(FieldType.OBJECT_ID)
    private String id;

    @NotBlank(message="Name cannot be empty")
    private String name;

    private String description;

    private List<Attribute> attributes;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    @DBRef
    private Category category;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

    private List<String> imgLinks = new ArrayList<>();

    private List<Review> reviews = new ArrayList<>();

    @NotBlank(message="Gender must not be blank")
    private String gender;
    @Data
    public static class Attribute {
        private String name;
        private String value;

        public Attribute(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

    @Data
    public static class Review {
        private int userId;
        private String description;
        private double rating;
    }


}
