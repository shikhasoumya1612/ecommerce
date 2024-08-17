package org.example.orderservice.dto;


import lombok.Data;

@Data
public class ProductBody {

    private String name;
    private String category;
    private String img;
    private int quantity;
    private double price;
}
