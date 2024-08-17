package org.example.productservice.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document(collection = "categories")
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Category name cannot be blank")
    private String name;

    private List<String> attributes;


    @NotBlank
    private String imgLink;

}
