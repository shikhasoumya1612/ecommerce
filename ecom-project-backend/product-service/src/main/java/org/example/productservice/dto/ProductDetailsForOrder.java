package org.example.productservice.dto;


import lombok.Data;

@Data
public class ProductDetailsForOrder {
    private String name;
    private String category;
    private String img;
    private int quantity;
    private double price;
}
