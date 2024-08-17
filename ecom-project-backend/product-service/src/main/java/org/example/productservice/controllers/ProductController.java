package org.example.productservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.example.productservice.dto.AddReviewBody;
import org.example.productservice.dto.CategoryUpdateBody;
import org.example.productservice.dto.ProductDetailsForOrder;
import org.example.productservice.dto.ProductUpdateBody;
import org.example.productservice.exception.customExceptions.BaseException;
import org.example.productservice.models.Category;
import org.example.productservice.models.Product;
import org.example.productservice.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin("*")
@RestController
@Validated
@RequestMapping(value = "/products")
public class ProductController {
    Logger logger = LoggerFactory.getLogger(Logger.class);

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // add product
    @PostMapping("/product/{categoryId}")
    public ResponseEntity<Object> addProduct(@Valid @RequestBody Product product, @PathVariable @NotBlank(message = "Category id should not be blank") String categoryId, HttpServletRequest request) {

        try {
            String token = productService.extractTokenFromRequest(request);

            if (!productService.checkAdmin(token)) {
                logger.error("addProduct - Authentication Error : Cannot be accesses");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }
            Product savedProduct = productService.addProduct(product, categoryId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product added successfully");
            response.put("product", savedProduct);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("addProduct - {}", exception.getMessage());
            throw exception;
        }

    }

    // get product by id
    @GetMapping("/products/{productId}")
    @ResponseBody
    public ResponseEntity<Object> getProductById(@PathVariable @NotBlank String productId) {

        try {
            Product product = productService.getProductById(productId);
            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("getProductById - {}", exception.getMessage());
            throw exception;
        }

    }

    // get product by id for order
    @GetMapping("/products/{productId}/detailsForOrder")
    @ResponseBody
    public ResponseEntity<Object> getProductDetailsForOrderById(@PathVariable @NotBlank String productId) {

        try {
            ProductDetailsForOrder product = productService.getProductDetailsForOrderById(productId);

            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("getProductById - {}", exception.getMessage());
            throw exception;
        }

    }

    @PostMapping("/products/{productId}/updateQuantity")
    @ResponseBody
    public ResponseEntity<Object> updateProductQuantity(@PathVariable @NotBlank String productId, @Valid @RequestBody ProductUpdateBody productUpdateBody) {

        try {

            if (productUpdateBody.getQuantity() == null) {
                logger.error("updateProductQuantity - {}", "Quantity cannot be null");
                throw new BaseException(HttpStatus.BAD_REQUEST, "Quantity cannot be null");

            }

            int quantity = Integer.parseInt(productUpdateBody.getQuantity());
            productService.updateProductQuantity(productId, quantity);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quantity updated successfully");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NumberFormatException exception) {
            logger.error("getProductById - {}", exception.getMessage());
            throw new BaseException(HttpStatus.BAD_REQUEST, "Quantity should be an integer");
        } catch (Exception exception) {
            logger.error("getProductById - {}", exception.getMessage());
            throw exception;
        }

    }

    // get product quantity by id
    @GetMapping("/products/{productId}/quantity")
    @ResponseBody
    public ResponseEntity<Object> getProductQuantityById(@PathVariable @NotBlank String productId) {
        try {
            int quantity = productService.getProductQuantityById(productId);

            Map<String, Object> response = new HashMap<>();
            response.put("quantity", quantity);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("getProductQuantityById - {}", exception.getMessage());
            throw exception;
        }
    }

    @GetMapping("/products")
    @ResponseBody
    public ResponseEntity<Object> getAllProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
    @RequestParam(required = false) ArrayList<String> gender) {

        try {




            if (minPrice == null) {
                minPrice = 0.0;
            }

            if (maxPrice == null) {
                maxPrice = Double.MAX_VALUE;
            }

            if (keyword == null) {
                keyword = "";
            }

            if (categoryId == null) {
                categoryId = "";
            }

            if (gender == null) {
                gender = new ArrayList<>();
            }


            List<Product> filteredProducts = productService.getFilteredProducts(
                    minPrice, maxPrice, keyword,categoryId,gender);

            return ResponseEntity.ok(filteredProducts);
        } catch (Exception exception) {
            logger.error("getAllProducts - {}", exception.getMessage());
            throw exception;
        }
    }


    // get all products of a category
    @GetMapping("/products/category/{categoryId}")
    @ResponseBody
    public ResponseEntity<Object> getProductsByCategory(@PathVariable @NotBlank String categoryId) {

        try {
            List<Product> products = productService.getProductsByCategory(categoryId);

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("getProductsByCategory - {}", exception.getMessage());
            throw exception;
        }

    }


    //get all categories

    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<Object> getAllCategories() {

        try {
            List<Category> categories = productService.getAllCategories();

            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("getAllCategory - {}", exception.getMessage());
            throw exception;
        }

    }

    // update product by id
    @PatchMapping("/product/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable @NotBlank String id, @RequestBody @Valid ProductUpdateBody updatedProduct, HttpServletRequest request) {

        try {
            String token = productService.extractTokenFromRequest(request);

            if (!productService.checkAdmin(token)) {
                logger.error("updateProduct - Authentication Error : Cannot be accessed");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }

            Product product = productService.updateProduct(id, updatedProduct);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product updated successfully");
            response.put("product", product);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("updateProduct - {}", exception.getMessage());
            throw exception;
        }

    }

    // delete product by id
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable @NotBlank String id, HttpServletRequest request) {

        try {
            String token = productService.extractTokenFromRequest(request);

            if (!productService.checkAdmin(token)) {
                logger.error("deleteProduct - Authentication Error : Cannot be accessed");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }

            productService.deleteProduct(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("deleteProduct - {}", exception.getMessage());
            throw exception;
        }

    }

    // add category
    @PostMapping("/category")
    public ResponseEntity<Object> addCategory(@Valid @RequestBody Category categoryBody, HttpServletRequest request) {

        try {
            String token = productService.extractTokenFromRequest(request);

            if (!productService.checkAdmin(token)) {
                logger.error("addCategory - Authentication Error : Cannot be accessed");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }
            Category category = productService.addCategory(categoryBody);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Category added successfully");
            response.put("category", category);


            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("addCategory - {}", exception.getMessage());
            throw exception;
        }
    }

    // get category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Object> getCategoryById(@PathVariable @NotBlank String categoryId) {

        try {
            Category category = productService.getCategoryById(categoryId);
            Map<String, Object> response = new HashMap<>();

            response.put("category", category);


            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("getCategoryById - {}", exception.getMessage());
            throw exception;
        }
    }

    // update category by id
    @PatchMapping("/category/{categoryId}")
    public ResponseEntity<Object> updateCategoryById(@PathVariable @NotBlank String categoryId, @Valid @RequestBody CategoryUpdateBody updatedCategory, HttpServletRequest request) {
        try {
            String token = productService.extractTokenFromRequest(request);

            if (!productService.checkAdmin(token)) {
                logger.error("updateCategoryById - Authentication Error : Cannot be accessed");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }

            Category category = productService.updateCategoryById(categoryId, updatedCategory);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Category updated successfully");
            response.put("category", category);


            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("updateCategoryById - {}", exception.getMessage());
            throw exception;
        }
    }

    // delete category by id
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<Object> deleteCategoryById(@PathVariable @NotBlank String categoryId, HttpServletRequest request) {

        try {
            String token = productService.extractTokenFromRequest(request);

            if (!productService.checkAdmin(token)) {
                logger.error("deleteCategoryById - Authentication Error : Cannot be accessed");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }

            productService.deleteCategoryById(categoryId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Category deleted successfully");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("deleteCategoryById - {}", exception.getMessage());
            throw exception;
        }
    }

    // Add Review
    @PostMapping("/product/{id}/review")
    public ResponseEntity<Object> addReview(@PathVariable @NotBlank String id, @Valid @RequestBody AddReviewBody addReviewBody, HttpServletRequest request) {

        try {
            String token = productService.extractTokenFromRequest(request);
            int userId = productService.getUserIdFromToken(token);


            Product product = productService.addReview(id,addReviewBody,userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review added successfully");
            response.put("product",product);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("addReview - {}", exception.getMessage());
            throw exception;
        }

    }


}