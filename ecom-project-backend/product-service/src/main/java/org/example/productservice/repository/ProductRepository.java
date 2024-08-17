package org.example.productservice.repository;

import org.example.productservice.models.Category;
import org.example.productservice.models.Product;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends MongoRepository<Product, String> {


    @Aggregation(pipeline = {
            "{$match: {$and:[{ price: { $gte: ?0 , $lte: ?1 } },{$or:[{brand: { $regex: ?2, $options:'i'}},{name: { $regex: ?2, $options:'i'}},{description: { $regex: ?2, $options:'i'}}]}]}}"
    })
    List<Product> findFilteredProducts(Double minPrice, Double maxPrice, String keyword);

    void deleteByCategory(Category category);


}