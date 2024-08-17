package org.example.productservice.services;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.productservice.dto.AddReviewBody;
import org.example.productservice.dto.CategoryUpdateBody;
import org.example.productservice.dto.ProductDetailsForOrder;
import org.example.productservice.dto.ProductUpdateBody;
import org.example.productservice.exception.customExceptions.BaseException;
import org.example.productservice.models.Category;
import org.example.productservice.models.Product;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    Logger logger = LoggerFactory.getLogger(Logger.class);

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    // extract token from cookies
    public String extractTokenFromRequest(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String authToken = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authToken = authHeader.replace("Bearer ", "");
            }

            if (authToken == null) {
                logger.error("extractTokenFromRequest : error while extracting token - Authentication token not present in header");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication token not found in header.");
            }

            return authToken;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("extractTokenFromRequest : error while extracting token - {}", exception.getMessage());
            throw exception;
        }
    }

    public int getUserIdFromToken(String authToken) {
        try {
            int userId = JwtTokenUtil.getUserIdFromToken(authToken);

            return userId;
        } catch (Exception exception) {
            logger.error("getUserDataFromToken : error while extracting user data from token - {}", exception.getMessage());
            throw exception;
        }
    }


    // check admin from token
    public Boolean checkAdmin(String authToken) {
        try {
            Map<String, Object> userData = JwtTokenUtil.getDataFromToken(authToken);
            if (userData.get("role").equals("ADMIN")) {
                return true;
            } else {
                logger.error("checkAdmin - Authorization Error : Cannot be accessed");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authorization Error : Cannot be accessed");
            }
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("checkAdmin - {}", exception.getMessage());
            throw exception;
        }
    }


    // add product
    @Transactional
    public Product addProduct(Product product, String categoryId) {
        try {
            Optional<Category> categoryData = categoryRepository.findById(categoryId);

            if (categoryData.isEmpty()) {
                logger.error("addProduct - Category not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Category not found");
            }

            Category category = categoryData.get();
            product.setCategory(category);

            // Filter product attributes based on category attribute names
            List<Product.Attribute> filteredAttributes = filterAttributes(product.getAttributes(), category.getAttributes());
            product.setAttributes(filteredAttributes);

            return productRepository.save(product);

        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("addProduct - {}", exception.getMessage());
            throw exception;
        }
    }

    // Helper method to filter attributes based on category attribute names
    private List<Product.Attribute> filterAttributes(List<Product.Attribute> allAttributes, List<String> allowedAttributes) {
        return allAttributes.stream()
                .filter(attribute -> allowedAttributes.contains(attribute.getName()))
                .collect(Collectors.toList());
    }


    // update product by id
    public Product updateProduct(String id, ProductUpdateBody updatedProduct) {
        try {
            Optional<Product> product = productRepository.findById(id);

            if (product.isEmpty()) {
                logger.error("updateProduct - Product not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Product not found");
            }

            // Update the product properties
            Product existingProduct = product.get();


            if (updatedProduct.getName() != null && updatedProduct.getName().length() >= 3) {
                existingProduct.setName(updatedProduct.getName());
            }

            if (updatedProduct.getDescription() != null) {
                existingProduct.setDescription(updatedProduct.getDescription());
            }


            if (updatedProduct.getPrice() != null) {
                if (updatedProduct.getPrice() <= 0) {
                    throw new BaseException(HttpStatus.BAD_REQUEST, "Price should be greater than 0");
                } else {
                    existingProduct.setPrice(updatedProduct.getPrice());
                }
            }

            if (updatedProduct.getAttributes() != null) {
                existingProduct.setAttributes(filterAttributes(updatedProduct.getAttributes(), existingProduct.getCategory().getAttributes()));
            }

            if (updatedProduct.getQuantity() != null) {
                existingProduct.setQuantity(Integer.parseInt(updatedProduct.getQuantity()));
            }


            // Save the updated product
            productRepository.save(existingProduct);

            return existingProduct;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("updateProduct - {}", exception.getMessage());
            throw exception;
        }
    }

    // delete product by id

    public void deleteProduct(String id) {
        try {
            Optional<Product> product = productRepository.findById(id);

            if (product.isEmpty()) {
                logger.error("delete product - Product not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Product not found");
            }

            productRepository.deleteById(id);
        } catch (ConversionFailedException exception) {
            logger.error("deleteProductById - Invalid product id");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid product id");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("delete product - {}", exception.getMessage());
            throw exception;
        }
    }


    // add category
    public Category addCategory(Category categoryBody) {
        try {
            return categoryRepository.save(categoryBody);
        } catch (Exception exception) {
            logger.error("add category - {}", exception.getMessage());
            throw exception;
        }
    }


    // get products by category
    public List<Product> getProductsByCategory(String categoryId) {
        try {
            List<Product> products = productRepository.findAll();
            products = products.stream()
                    .filter(product -> product.getCategory().getId().equals(categoryId))
                    .toList();

            if (products.isEmpty()) {
                logger.error("getProductsByCategory - Category not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Category not found");
            }

            return products;


        } catch (ConversionFailedException exception) {
            logger.error("getProductsByCategory - Invalid category id");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid category id");
        } catch (Exception exception) {
            logger.error("getProductByCategory - {}", exception.getMessage());
            throw exception;
        }
    }

    public List<Product> getFilteredProducts(Double minPrice, Double maxPrice, String keyword,String categoryId,ArrayList<String> gender) {
        try {

            List<Product> products = productRepository.findFilteredProducts(minPrice, maxPrice, keyword);
            if(!categoryId.isBlank()) {
                Optional<Category> category = categoryRepository.findById(categoryId);
                if (category.isEmpty()) {
                    throw new BaseException(HttpStatus.NOT_FOUND, "Category not found");
                }


                products = products.stream().filter(product -> product.getCategory().getId().equals(categoryId)).toList();
            }

            if(!gender.isEmpty()) {
                products = products.stream().filter(product -> gender.contains(product.getGender())).toList();
            }

            return products;

        } catch (Exception exception) {
            logger.error("getFilteredProducts - {}", exception.getMessage());
            throw exception;
        }
    }

    // get product by id
    public Product getProductById(String productId) {
        try {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isEmpty()) {
                logger.error("getProductById - Product not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Product not found");
            }

            return product.get();
        } catch (ConversionFailedException exception) {
            logger.error("getProductById - Invalid product id");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid product id");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getProductById - {}", exception.getMessage());
            throw exception;
        }

    }

    public ProductDetailsForOrder getProductDetailsForOrderById(String productId) {
        try {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isEmpty()) {
                logger.error("getProductById - Product not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Product not found");
            }

            ProductDetailsForOrder productDetails = new ProductDetailsForOrder();
            productDetails.setName(product.get().getName());
            productDetails.setCategory(product.get().getCategory().getName());
            productDetails.setQuantity(product.get().getQuantity());
            productDetails.setPrice(product.get().getPrice());

            List<String> imgLinks = product.get().getImgLinks();

            if (imgLinks == null) {
                imgLinks = new ArrayList<>();
            }
            productDetails.setImg(imgLinks.isEmpty() ? "default-link" : imgLinks.get(0));

            return productDetails;
        } catch (ConversionFailedException exception) {
            logger.error("getProductById - Invalid product id");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid product id");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getProductById - {}", exception.getMessage());
            throw exception;
        }
    }

    // get product quantity by id
    public int getProductQuantityById(String productId) {
        try {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isEmpty()) {
                logger.error("getProductQuantityById - Product not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Product not found");
            }

            return product.get().getQuantity();
        } catch (ConversionFailedException exception) {
            logger.error("getProductQuantityById - Invalid product id");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid product id");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getProductQuantityById - {}", exception.getMessage());
            throw exception;
        }
    }

    public void updateProductQuantity(String productId, int quantity) {
        try {
            Optional<Product> productDetails = productRepository.findById(productId);
            if (productDetails.isEmpty()) {
                logger.error("getProductQuantityById - Product not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Product not found");
            }

            Product product = productDetails.get();
            product.setQuantity(quantity);

            productRepository.save(product);

        } catch (DataIntegrityViolationException exception) {
            logger.error("getProductQuantityById - Invalid quantity entered");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid quantity entered");

        } catch (ConversionFailedException exception) {
            logger.error("getProductQuantityById - Invalid product id");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid product id");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getProductQuantityById - {}", exception.getMessage());
            throw exception;
        }
    }

    // get category by id
    public Category getCategoryById(String categoryId) {
        try {
            Optional<Category> category = categoryRepository.findById(categoryId);
            if (category.isEmpty()) {
                logger.error("getCategoryById - Category not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Category not found");
            }

            return category.get();

        } catch (ConversionFailedException exception) {
            logger.error("getCategoryById - Invalid category id");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid category id");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getCategoryById - {}", exception.getMessage());
            throw exception;
        }


    }

    // update category by id
    public Category updateCategoryById(String categoryId, CategoryUpdateBody updatedCategory) {
        try {
            Optional<Category> category = categoryRepository.findById(categoryId);

            if (category.isEmpty()) {
                logger.error("updateCategoryById - Category not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Category not found");
            }

            // Update the product properties
            Category existingCategory = category.get();

            if (updatedCategory.getName() != null) {
                existingCategory.setName(updatedCategory.getName());
            }


            return categoryRepository.save(existingCategory);
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("updateCategoryById - {}", exception.getMessage());
            throw exception;
        }


    }

    // delete category by id
    public void deleteCategoryById(String categoryId) {
        try {
            Optional<Category> category = categoryRepository.findById(categoryId);

            if (category.isEmpty()) {
                logger.error("deleteCategoryById - Category not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Category not found");
            }

            categoryRepository.deleteById(categoryId);
            productRepository.deleteByCategory(category.get());

        } catch (ConversionFailedException exception) {
            logger.error("deleteCategoryById - Invalid category id");
            throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid category id");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("deleteCategoryById - {}", exception.getMessage());
            throw exception;
        }
    }


    public List<Category> getAllCategories() {
        try {
            return categoryRepository.findAll();

        } catch (Exception exception) {
            logger.error(" getAllCategories - {}", exception.getMessage());
            throw exception;
        }
    }

    public Product addReview(String id, AddReviewBody addReviewBody, int userId) {
        try {
            Optional<Product> productDetails = productRepository.findById(id);
            if (productDetails.isEmpty()) {
                logger.error("addReview - Product not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Product not found");
            }

            Product product = productDetails.get();

            Product.Review review = new Product.Review();
            review.setUserId(userId);
            review.setRating(addReviewBody.getRating());
            review.setDescription(addReviewBody.getDescription());

            List<Product.Review> reviews = product.getReviews();
            reviews.add(review);
            product.setReviews(reviews);

            productRepository.save(product);

            return product;



        } catch (Exception exception) {
            logger.error("addReview - {}", exception.getMessage());
            throw exception;
        }
    }



}
