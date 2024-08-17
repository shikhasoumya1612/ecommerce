package org.example.cartservice.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.cartservice.dto.CartItemBodyDto;
import org.example.cartservice.entities.Cart;
import org.example.cartservice.services.CartService;
import org.example.cartservice.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@CrossOrigin("*")
@RestController
@RequestMapping("/cart")
@Validated
public class CartController {

    private Logger logger = LoggerFactory.getLogger(Logger.class);
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping("/addToCart")
    public ResponseEntity<Object> addToCart(@Valid @RequestBody CartItemBodyDto addToCartBody, HttpServletRequest request) {


        try {
            String token = cartService.extractTokenFromRequest(request);

            int userId = JwtUtil.getUserIdFromToken(token);

            cartService.addToCart(userId, addToCartBody);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product added successfully");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("fetchCart - error while fetching cart : {}", exception.getMessage());
            throw exception;
        }


    }

    @GetMapping("")
    public ResponseEntity<Object> fetchCart(HttpServletRequest request) {
        try {
            String token = cartService.extractTokenFromRequest(request);

            int userId = JwtUtil.getUserIdFromToken(token);

            Cart cart = cartService.fetchCart(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart fetched successfully");
            response.put("cart", cart);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("fetchCart - error while fetching cart : {}", exception.getMessage());
            throw exception;
        }
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Object> removeFromCart(@RequestBody @Valid CartItemBodyDto
                                                         addToCartBody, HttpServletRequest request) {

        try {
            String token = cartService.extractTokenFromRequest(request);

            int userId = JwtUtil.getUserIdFromToken(token);

            cartService.removeFromCart(userId, addToCartBody);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Removed from cart successfully");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("removeFromCart - error while removing from cart : {}", exception.getMessage());
            throw exception;
        }

    }


    @GetMapping("/clearCart")
    public ResponseEntity<Object> clearCart(HttpServletRequest request) {

        try {
            String token = cartService.extractTokenFromRequest(request);

            int userId = JwtUtil.getUserIdFromToken(token);

            cartService.clearCart(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart cleared successfully");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("clearCart - error while clearing cart : {}", exception.getMessage());
            throw exception;
        }

    }

}
