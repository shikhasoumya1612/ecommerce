package org.example.productservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.productservice.dto.AddReviewBody;
import org.example.productservice.dto.CategoryUpdateBody;
import org.example.productservice.dto.ProductDetailsForOrder;
import org.example.productservice.dto.ProductUpdateBody;
import org.example.productservice.exception.customExceptions.BaseException;
import org.example.productservice.models.Category;
import org.example.productservice.models.Product;
import org.example.productservice.services.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;


    private ProductController productController;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();


    // add product
    @Test
    void addProduct_success() throws Exception {

        List<Product.Attribute> attributes = new ArrayList<>();
        attributes.add(new Product.Attribute("Material", "Sustainable Materials"));

        Category category = new Category();
        List<String> imgLinks = new ArrayList<>();
        List<Product.Review> reviews = new ArrayList<>();


        Product product = new Product("1", "Luka 2 PF", "You bring the speed", attributes, 1100.00, category, 100, imgLinks, reviews,"gender");

        when(productService.addProduct(any(), anyString())).thenReturn(product);

        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");

        when(productService.checkAdmin(any())).thenReturn(true);

        this.mockMvc.perform(post("/products/product/something").header("authorization", "Bearer correctToken").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(product))).andExpect(status().isCreated()).andExpect(jsonPath("$.product").exists());
    }

    @Test
    void addProduct_authorizationError() throws Exception {

        List<Product.Attribute> attributes = new ArrayList<>();
        attributes.add(new Product.Attribute("Material", "Sustainable Materials"));

        Category category = new Category();
        List<String> imgLinks = new ArrayList<>();
        List<Product.Review> reviews = new ArrayList<>();


        Product product = new Product("1", "Luka 2 PF", "You bring the speed", attributes, 1100.00, category, 100, imgLinks, reviews,"gender");


        when(productService.addProduct(any(), anyString())).thenReturn(product);

        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");

        when(productService.checkAdmin(any())).thenReturn(false);

        this.mockMvc.perform(post("/products/product/something").header("authorization", "Bearer correctToken").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(product))).andExpect(status().isUnauthorized());
    }

    @Test
    void addProduct_badRequest() throws Exception {

        List<Product.Attribute> attributes = new ArrayList<>();
        attributes.add(new Product.Attribute("Material", "Sustainable Materials"));

        Category category = new Category();
        List<String> imgLinks = new ArrayList<>();

        List<Product.Review> reviews = new ArrayList<>();


        Product product = new Product("1", "Luka 2 PF", "You bring the speed", attributes, 1100.00, category, 100, imgLinks, reviews,"gender");


        when(productService.addProduct(any(), anyString())).thenReturn(product);

        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");

        when(productService.checkAdmin(any())).thenReturn(true);

        this.mockMvc.perform(post("/products/product/something").header("authorization", "Bearer correctToken").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(product))).andExpect(status().isBadRequest());

    }

    @Test
    void addProduct_invalidParamters() throws Exception {

        List<Product.Attribute> attributes = new ArrayList<>();
        attributes.add(new Product.Attribute("Material", "Sustainable Materials"));

        Category category = new Category();
        List<String> imgLinks = new ArrayList<>();
        List<Product.Review> reviews = new ArrayList<>();


        Product product = new Product("1", "Luka 2 PF", "You bring the speed", attributes, 1100.00, category, 100, imgLinks, reviews,"gender");

//        when(productService.addProduct(any(), anyString())).thenReturn(product);
//
//        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
//
//        when(productService.checkAdmin(any())).thenReturn(false);

        this.mockMvc.perform(post("/products/product/    ").header("authorization", "Bearer correctToken").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(product))).andExpect(status().isInternalServerError());

    }

    // get product by id
    @Test
    void getProductById_success() throws Exception {
        List<Product.Attribute> attributes = new ArrayList<>();
        attributes.add(new Product.Attribute("Material", "Sustainable Materials"));

        Category category = new Category();
        List<String> imgLinks = new ArrayList<>();

        List<Product.Review> reviews = new ArrayList<>();


        Product product = new Product("1", "Luka 2 PF", "You bring the speed", attributes, 1100.00, category, 100, imgLinks, reviews,"gender");


        when(productService.getProductById(any())).thenReturn(product);

        this.mockMvc.perform(get("/products/products/something")).andExpect(status().isOk());

    }

    @Test
    void getProductById_notFound() throws Exception {

        when(productService.getProductById(any())).thenThrow(new BaseException(HttpStatus.NOT_FOUND, "Product not found"));
        this.mockMvc.perform(get("/products/products/something")).andExpect(status().isNotFound());

    }


    @Test
    void getProductById_invalidParameter() throws Exception {

        this.mockMvc.perform(get("/products/products/    ")).andExpect(status().isInternalServerError());


        Mockito.verify(productService, Mockito.times(0)).getProductById(anyString());
    }


    // update quantity for product
    @Test
    void updateProductQuantity_success() throws Exception {

        ProductUpdateBody productUpdateBody = new ProductUpdateBody();
        productUpdateBody.setQuantity("100");

        String productId = "something";
        int quantity = 100;

        doNothing().when(productService).updateProductQuantity(productId, quantity);

        this.mockMvc.perform(post("/products/products/" + productId + "/updateQuantity").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(productUpdateBody))).andExpect(status().isOk()).andExpect(jsonPath("$.message", Matchers.is("Quantity updated successfully")));

        Mockito.verify(productService, Mockito.times(0)).getProductById(anyString());
    }


    @Test
    void updateProductQuantity_invalidParameter() throws Exception {

        ProductUpdateBody productUpdateBody = new ProductUpdateBody();
        productUpdateBody.setQuantity("100");

        String productId = "  ";
        int quantity = 100;


        this.mockMvc.perform(post("/products/products/" + productId + "/updateQuantity").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(productUpdateBody))).andExpect(status().isInternalServerError()).andExpect(jsonPath("$.message", Matchers.is("updateProductQuantity.productId: must not be blank")));

        Mockito.verify(productService, Mockito.times(0)).getProductById(anyString());
    }

    @Test
    void updateProductQuantity_badRequest() throws Exception {

        ProductUpdateBody productUpdateBody = new ProductUpdateBody();

        String productId = "something";

        this.mockMvc.perform(post("/products/products/" + productId + "/updateQuantity").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(productUpdateBody))).andExpect(status().isBadRequest());

    }

    // get product by id for order
    @Test
    void getProductDetailsForOrderById_success() throws Exception {

        ProductDetailsForOrder productDetails = new ProductDetailsForOrder();


        when(productService.getProductDetailsForOrderById(anyString())).thenReturn(productDetails);


        mockMvc.perform(get("/products/products/something/detailsForOrder"))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void getProductDetailsForOrderById_invalidParameter() throws Exception {

        ProductDetailsForOrder productDetails = new ProductDetailsForOrder();


        when(productService.getProductDetailsForOrderById(anyString())).thenReturn(productDetails);


        mockMvc.perform(get("/products/products/   /detailsForOrder"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

    }


    // delete product by id
    @Test
    void deleteProduct_success() throws Exception {

        String productId = "something";
        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);

        mockMvc.perform(delete("/products/product/" + productId)
                        .header("authorization", "Bearer correctToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }


    @Test
    void deleteProduct_invalidParameter() throws Exception {

        String productId = "    ";
        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);

        mockMvc.perform(delete("/products/product/" + productId)
                        .header("authorization", "Bearer correctToken"))
                .andExpect(status().isInternalServerError());

    }


    // add category
    @Test
    void addCategory_success() throws Exception {
        Category category = new Category();
        category.setName("category");
        category.setImgLink("link");

        when(productService.addCategory(any())).thenReturn(category);
        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);

        mockMvc.perform(post("/products/category")
                        .header("authorization", "Bearer correctToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").exists());
    }

    @Test
    void addCategory_badRequest() throws Exception {
        Category category = new Category();
        category.setImgLink("link");

        when(productService.addCategory(any())).thenReturn(category);
        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);

        mockMvc.perform(post("/products/category")
                        .header("authorization", "Bearer correctToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(category)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Category name cannot be blank"));
    }


    // Add Review
    @Test
    void addReview_success() throws Exception {
        AddReviewBody addReviewBody = new AddReviewBody();

        Product product = new Product();

        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.getUserIdFromToken("correctToken")).thenReturn(1);
        when(productService.addReview(anyString(), any(), Mockito.anyInt())).thenReturn(product);

        mockMvc.perform(post("/products/product/something/review")
                        .header("authorization", "Bearer correctToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(addReviewBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Review added successfully"))
                .andExpect(jsonPath("$.product").exists());
    }

    // Update category by ID
    @Test
    void updateCategoryById_success() throws Exception {
        CategoryUpdateBody updateBody = new CategoryUpdateBody();
        updateBody.setName("Updated Category");

        Category updatedCategory = new Category();
        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);
        when(productService.updateCategoryById(anyString(), any(CategoryUpdateBody.class))).thenReturn(updatedCategory);

        mockMvc.perform(patch("/products/category/{categoryId}", "categoryId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category updated successfully"));
    }

    @Test
    void updateCategoryById_unauthorized() throws Exception {
        CategoryUpdateBody updateBody = new CategoryUpdateBody();
        updateBody.setName("Updated Category");

        Category updatedCategory = new Category();
        when(productService.extractTokenFromRequest(any())).thenReturn("incorrectToken");
        when(productService.checkAdmin("incorrectToken")).thenReturn(false);
        when(productService.updateCategoryById(anyString(), any(CategoryUpdateBody.class))).thenReturn(updatedCategory);

        mockMvc.perform(patch("/products/category/{categoryId}", "categoryId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(updateBody)))
                .andExpect(status().isUnauthorized());

    }



    // Delete category by ID
    @Test
    void deleteCategoryById_success() throws Exception {
        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);

        mockMvc.perform(delete("/products/category/{categoryId}", "categoryId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    @Test
    void deleteCategoryById_unauthorized() throws Exception {
        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(false);

        mockMvc.perform(delete("/products/category/{categoryId}", "categoryId"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteCategoryById_invalidParameter() throws Exception {
        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);

        mockMvc.perform(delete("/products/category/   ", "categoryId"))
                .andExpect(status().isInternalServerError());
    }



    // Get all categories
    @Test
    void getAllCategories_success() throws Exception {
        List<Category> categories = new ArrayList<>();
        when(productService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(categories.size()));
    }


    // Update product by ID
    @Test
    void updateProduct_success() throws Exception {
        ProductUpdateBody updateBody = new ProductUpdateBody();
        updateBody.setQuantity("100");

        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);


        Product updatedProduct = new Product();
        when(productService.updateProduct(anyString(), any(ProductUpdateBody.class))).thenReturn(updatedProduct);

        mockMvc.perform(patch("/products/product/{id}", "productId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product updated successfully"));
    }


    @Test
    void updateProduct_unauthorized() throws Exception {
        ProductUpdateBody updateBody = new ProductUpdateBody();
        updateBody.setQuantity("100");

        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(false);


        Product updatedProduct = new Product();
        when(productService.updateProduct(anyString(), any(ProductUpdateBody.class))).thenReturn(updatedProduct);

        mockMvc.perform(patch("/products/product/{id}", "productId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(updateBody)))
                .andExpect(status().isUnauthorized());

    }


    @Test
    void updateProduct_invalidParameter() throws Exception {
        ProductUpdateBody updateBody = new ProductUpdateBody();
        updateBody.setQuantity("100");

        when(productService.extractTokenFromRequest(any())).thenReturn("correctToken");
        when(productService.checkAdmin("correctToken")).thenReturn(true);


        Product updatedProduct = new Product();
        when(productService.updateProduct(anyString(), any(ProductUpdateBody.class))).thenReturn(updatedProduct);

        mockMvc.perform(patch("/products/product/    ", "productId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(updateBody)))
                .andExpect(status().isInternalServerError());

    }




    // Get product quantity by ID
    @Test
    void getProductQuantityById_success() throws Exception {
        int quantity = 10;
        when(productService.getProductQuantityById(anyString())).thenReturn(quantity);

        mockMvc.perform(get("/products/products/{productId}/quantity", "productId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(quantity));
    }

    @Test
    void getProductQuantityById_invalidParameter() throws Exception {
        int quantity = 10;
        when(productService.getProductQuantityById(anyString())).thenReturn(quantity);

        mockMvc.perform(get("/products/products/    /quantity", "productId"))
                .andExpect(status().isInternalServerError());
    }


    // Get all products of a category
    @Test
    void getProductsByCategory_success() throws Exception {
        List<Product> products = new ArrayList<>();
        when(productService.getProductsByCategory(anyString())).thenReturn(products);

        mockMvc.perform(get("/products/products/category/{categoryId}", "categoryId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(products.size()));
    }

    @Test
    void getProductsByCategory_invalidParameter() throws Exception {
        List<Product> products = new ArrayList<>();
        when(productService.getProductsByCategory(anyString())).thenReturn(products);

        mockMvc.perform(get("/products/products/category/    ", "categoryId"))
                .andExpect(status().isInternalServerError());

    }


    // Get all products
    @Test
    void getAllProducts_success() throws Exception {
        List<Product> products = new ArrayList<>();  // Create mock products as needed
        when(productService.getFilteredProducts(Mockito.anyDouble(),Mockito.anyDouble(), anyString(), anyString(),new ArrayList<>())).thenReturn(products);

        mockMvc.perform(get("/products/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(products.size()));
    }

}