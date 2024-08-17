package org.example.cartservice.services;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.cartservice.dto.CartItemBodyDto;
import org.example.cartservice.dto.Product;
import org.example.cartservice.entities.Cart;
import org.example.cartservice.entities.CartItem;
import org.example.cartservice.exception.customExceptions.BaseException;
import org.example.cartservice.repository.CartItemRepository;
import org.example.cartservice.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private Logger logger = LoggerFactory.getLogger(Logger.class);

    private final WebClient webClient;

    @Autowired
    public CartService(WebClient webClient, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.webClient = webClient;
    }

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

    public Product getProductDetails(String productId) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.pathSegment("products", "{productId}", "quantity").build(productId))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, "Service down. Try again later.")))
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> Mono.error(new BaseException(HttpStatus.BAD_REQUEST, "Invalid Product Id")))
                    .bodyToMono(Product.class).block();


        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getProductDetails - error while fetching products : {}", exception.getMessage());
            throw exception;
        }
    }

    public void addToCart(int userId, CartItemBodyDto addToCartBody) {

        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> createNewCart(userId));


            Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProductAndSize(cart.getId(), addToCartBody.getProductId(),addToCartBody.getSize());

            Product product = getProductDetails(addToCartBody.getProductId());

            int quantityAvailable = product.getQuantity();

            if (quantityAvailable < addToCartBody.getQuantity()) {
                logger.error("addToCart - error while adding to cart : Product has limited stock");
                throw new BaseException(HttpStatus.BAD_REQUEST, "Product has limited stock");
            }

            if (existingCartItem.isPresent()) {
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + addToCartBody.getQuantity());
                cartItemRepository.save(cartItem);
            } else {
                CartItem newCartItem = new CartItem();
                newCartItem.setQuantity(addToCartBody.getQuantity());
                newCartItem.setProductId(addToCartBody.getProductId());
                newCartItem.setSize(addToCartBody.getSize());

                newCartItem.setCart(cart);

                cartItemRepository.save(newCartItem);
            }
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("addToCart - error while adding to cart : {}", exception.getMessage());
            throw exception;
        }

    }

    public Cart fetchCart(int userId) {
        try {
            return cartRepository.findByUserId(userId)
                    .orElseGet(() -> createNewCart(userId));
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("fetchCart - error while fetching cart : {}", exception.getMessage());
            throw exception;
        }
    }

    private Cart createNewCart(int userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    public void removeFromCart(int userId, CartItemBodyDto addToCartBody) {
        try {

            if (addToCartBody.getQuantity() < 1) {
                logger.error("addToCart - error while adding to cart : Quantity should not be less than 1");
                throw new BaseException(HttpStatus.BAD_REQUEST, "Quantity should not be less than 1");
            }

            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> createNewCart(userId));


            Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProductAndSize(cart.getId(), addToCartBody.getProductId(),addToCartBody.getSize());

            if (existingCartItem.isPresent() && existingCartItem.get().getQuantity() >= addToCartBody.getQuantity()) {
                CartItem cartItem = existingCartItem.get();
                int newQuantity = cartItem.getQuantity() - addToCartBody.getQuantity();

                if (newQuantity == 0) {
                    cartItemRepository.deleteById(cartItem.getId());
                } else {
                    cartItem.setQuantity(cartItem.getQuantity() - addToCartBody.getQuantity());
                    cartItemRepository.save(cartItem);
                }

            } else {
                throw new BaseException(HttpStatus.BAD_REQUEST, "Cannot decrease quantity");
            }
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("removeFromCart - error while removing from cart : {}", exception.getMessage());
            throw exception;
        }
    }


    public void clearCart(int userId) {
        try {
            Optional<Cart> cart = cartRepository.findByUserId(userId);

            cart.ifPresent(value -> cartRepository.deleteById(value.getId()));

            createNewCart(userId);
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("clear cart - error while clearing cart : {}", exception.getMessage());
            throw exception;
        }
    }
}
