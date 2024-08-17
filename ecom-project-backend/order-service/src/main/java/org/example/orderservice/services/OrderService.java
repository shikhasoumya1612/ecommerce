package org.example.orderservice.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.orderservice.dto.*;
import org.example.orderservice.entities.Order;
import org.example.orderservice.entities.OrderItem;
import org.example.orderservice.entities.OrderStatus;
import org.example.orderservice.entities.PaymentStatus;
import org.example.orderservice.exception.customExceptions.BaseException;
import org.example.orderservice.repository.OrderItemRepository;
import org.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private Logger logger = LoggerFactory.getLogger(Logger.class);

    private final WebClient productClient;
    private final WebClient userClient;

    @Autowired
    public OrderService(WebClient userClient, WebClient productClient, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userClient = userClient;
        this.productClient = productClient;
    }


    // function to extract token from cookies
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


    // get payment method from user-service
    public PaymentBody getPaymentDetails(int paymentMethodId) {
        try {
            return userClient.get()
                    .uri(uriBuilder -> uriBuilder.pathSegment("paymentMethods", "{paymentMethodId}", "string").build(paymentMethodId))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, "Service down. Try again later.")))
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> Mono.error(new BaseException(HttpStatus.BAD_REQUEST, "Invalid payment method id")))
                    .bodyToMono(PaymentBody.class).block();


        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getPaymentDetails - error while fetching payment details : {}", exception.getMessage());
            throw exception;
        }
    }

    // get address from user-service
    private AddressBody getAddressDetails(int addressId) {
        try {
            return userClient.get()
                    .uri(uriBuilder -> uriBuilder.pathSegment("addresses", "{addressId}", "string").build(addressId))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, "Service down. Try again later.")))
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> Mono.error(new BaseException(HttpStatus.BAD_REQUEST, "Invalid address id")))
                    .bodyToMono(AddressBody.class).block();


        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getAddressDetails - error while fetching address : {}", exception.getMessage());
            throw exception;
        }
    }

    // get product details from product-service
    private ProductBody getProductDetails(String productId) {
        try {
            return productClient.get()
                    .uri(uriBuilder -> uriBuilder.pathSegment("products", "{productId}", "detailsForOrder").build(productId))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, "Service down. Try again later.")))
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> Mono.error(new BaseException(HttpStatus.BAD_REQUEST, "Invalid product id")))
                    .bodyToMono(ProductBody.class).block();


        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getAddressDetails - error while fetching address : {}", exception.getMessage());
            throw exception;
        }
    }

    // change quantity using product-service
    private void updateQuantity(String productId, int quantity) {
        try {
            ProductUpdateBody productUpdateBody = new ProductUpdateBody();
            productUpdateBody.setQuantity("" + quantity);
            productClient.post()

                    .uri(uriBuilder -> uriBuilder.pathSegment("products", "{productId}", "updateQuantity").build(productId))
                    .body(BodyInserters.fromValue(productUpdateBody))
                    .retrieve()

                    .toBodilessEntity()
                    .subscribe(
                            responseEntity -> {
                                // Success response handled here
                                HttpStatusCode status = responseEntity.getStatusCode();
                                System.out.println(status);
                            },

                            error -> {
                                throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                            }
                    );

        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("getAddressDetails - error while fetching address : {}", exception.getMessage());
            throw exception;
        }
    }


    // create order
    public void createOrder(int userId, OrderRequestBody orderRequestBody) {

        try {
            if (orderRequestBody.getAddressId() == 0) {
                throw new BaseException(HttpStatus.BAD_REQUEST, "Address id field cannot be empty");

            }

            if (orderRequestBody.getPaymentMethodId() == 0) {
                throw new BaseException(HttpStatus.BAD_REQUEST, "Payment method id field cannot be empty");

            }

            // creating new order
            Order order = new Order();
            order.setUserId(userId);
            order.setAddressDetails(getAddressDetails(orderRequestBody.getAddressId()).getAddressDetails());
            order.setPaymentDetails(getPaymentDetails(orderRequestBody.getPaymentMethodId()).getPaymentDetails());
            order.setOrderStatus(OrderStatus.PLACED);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderDate(LocalDateTime.now());
            order.setTotalPrice(0.01);

            orderRepository.save(order);

            double totalPrice = 0.0;

            for (OrderItemRequestBody orderItemRequest : orderRequestBody.getOrderItemList()) {
                OrderItem orderItem = new OrderItem();

                // fetching individual product details
                ProductBody productDetails = getProductDetails(orderItemRequest.getProductId());
                if (productDetails.getQuantity() < orderItemRequest.getQuantity()) {
                    orderRepository.deleteById(order.getId());
                    logger.error("createOrder : error while saving order - {}", productDetails.getName() + " has limited stock");
                    throw new BaseException(HttpStatus.BAD_REQUEST, productDetails.getName() + " has limited stock");
                }

                // setting order item details
                orderItem.setImg(productDetails.getImg());
                orderItem.setProductName(productDetails.getName());
                orderItem.setCategory(productDetails.getCategory());
                orderItem.setPrice(productDetails.getPrice());
                orderItem.setProductId(orderItemRequest.getProductId());
                orderItem.setQuantity(orderItemRequest.getQuantity());
                orderItem.setPrice(productDetails.getPrice());
                orderItem.setOrder(order);
                orderItem.setSize(orderItemRequest.getSize());

                totalPrice+=productDetails.getPrice();


                // save order item
                orderItemRepository.save(orderItem);

                // update quantity after order item is saved
                updateQuantity(orderItemRequest.getProductId(), productDetails.getQuantity() - orderItemRequest.getQuantity());



            }
            order.setTotalPrice(totalPrice*1.1);
            orderRepository.save(order);
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("createOrder : error while saving order - {}", exception.getMessage());
            throw exception;
        }


    }


    // get all orders
    public List<Order> getAllOrders(int userId) {
        try {
            return orderRepository.findByUserId(userId);
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("createOrder : error while saving order - {}", exception.getMessage());
            throw exception;
        }
    }



    // get orders by id
    public Order getOrderById(int userId, int orderId) {
        try {

            Optional<Order> order = orderRepository.findById(orderId);
            if (order.isEmpty() || order.get().getUserId() != userId) {
                logger.error("createOrder : error while fetching order - {}", "Order not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Order not found");
            }

            return order.get();

        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("createOrder : error while fetching order - {}", exception.getMessage());
            throw exception;
        }
    }
}
