package org.example.orderservice.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.orderservice.dto.OrderRequestBody;
import org.example.orderservice.entities.Order;
import org.example.orderservice.services.OrderService;
import org.example.orderservice.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")

@RestController
@RequestMapping("/order")
@Validated
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(Logger.class);
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    // create order
    @PostMapping("/createOrder")
    public ResponseEntity<Object> createOrder(@RequestBody @Valid OrderRequestBody orderRequestBody, HttpServletRequest request) {
        try {
            String token = orderService.extractTokenFromRequest(request);

            int userId = JwtUtil.getUserIdFromToken(token);

            orderService.createOrder(userId, orderRequestBody);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order placed successfully");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("createOrder - error while creating order : {}", exception.getMessage());
            throw exception;
        }


    }

    // get all orders
    @GetMapping("/all")
    public ResponseEntity<Object> getAllOrders(HttpServletRequest request) {
        try {
            String token = orderService.extractTokenFromRequest(request);

            int userId = JwtUtil.getUserIdFromToken(token);

            List<Order> orders = orderService.getAllOrders(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Orders fetched successfully");
            response.put("orders", orders);


            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("createOrder - error while creating order : {}", exception.getMessage());
            throw exception;
        }


    }


    // get order by id
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable @NotNull(message="Order id cannot be null") int orderId, HttpServletRequest request) {
        try {
            String token = orderService.extractTokenFromRequest(request);

            int userId = JwtUtil.getUserIdFromToken(token);

            Order order = orderService.getOrderById(userId,orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Orders fetched successfully");
            response.put("order", order);


            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception exception) {
            logger.error("createOrder - error while creating order : {}", exception.getMessage());
            throw exception;
        }


    }


}
