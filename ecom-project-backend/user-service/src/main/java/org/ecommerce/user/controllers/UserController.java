package org.ecommerce.user.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.ecommerce.user.dto.LoginRequest;
import org.ecommerce.user.dto.UserResponseBody;
import org.ecommerce.user.entities.Address;
import org.ecommerce.user.entities.PaymentMethod;
import org.ecommerce.user.entities.Role;
import org.ecommerce.user.entities.User;
import org.ecommerce.user.exception.customExceptions.BaseException;
import org.ecommerce.user.services.UserService;
import org.ecommerce.user.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@CrossOrigin("*")
@RestController
@Validated
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    Logger logger = LoggerFactory.getLogger(Logger.class);


    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users - admin
    @GetMapping("")
    public ResponseEntity<Object> retrieveAllUsers(HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

            if (!user.getRole().equals(Role.ADMIN)) {
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Users fetched successfully");
            response.put("users", userService.retrieveAllUsers());


            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("retrieve all users : error while fetching all users - {}", exception.getMessage());
            throw exception;
        }
    }

    // Get user by id
    @GetMapping("/{userId}")
    public ResponseEntity<Object> retrieveUserById(@PathVariable int userId, HttpServletRequest request) {
        try {

            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

//            if (!user.getRole().equals(Role.ADMIN)) {
//                logger.error("retrieve user by id : this api can only be accessed by ADMIN");
//                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
//            }


            User userDetails = userService.retrieveUserById(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User fetched successfully");
            response.put("user", userDetails);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error("retrieve user by id : error while fetching user - {}", exception.getMessage());
            throw exception;
        }

    }

    // Get my user data
    @GetMapping("/my-data")
    public ResponseEntity<Object> retrieveMyData(HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User userData = userService.getUserDataFromToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User details fetched successfully");
            response.put("user",new UserResponseBody(userData.getEmail(), userData.getRole(), userData.getName(), userData.getId(), userData.getAddresses(),userData.getImgLink(),userData.getPaymentMethods()));


            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception exception) {
            logger.error("retrieveMyData : error while fetching user data - {}", exception.getMessage());
            throw exception;
        }

    }

    // Register user
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody User user) {

        try {
            User userData = userService.createUser(user);
            UserResponseBody savedUser = new UserResponseBody(userData.getEmail(), userData.getRole(), userData.getName(), userData.getId(), userData.getAddresses(),userData.getImgLink(),userData.getPaymentMethods());


            // Include the token in the response body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "User registered successfully");
            responseBody.put("user", savedUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (Exception exception) {
            logger.error("registerUser : error while registering user data - {}", exception.getMessage());
            throw exception;
        }
    }

    // Login user
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginRequest requestBody, HttpServletResponse response) {

        try {
            User userData = userService.loginUser(requestBody);

            UserResponseBody user = new UserResponseBody(userData.getEmail(), userData.getRole(), userData.getName(), userData.getId(), userData.getAddresses(),userData.getImgLink(),userData.getPaymentMethods());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "User logged in successfully");
            responseBody.put("user", user);

            // Include the token in cookies
            String token = JwtTokenUtil.generateToken(user);
            responseBody.put("token", token);

            return ResponseEntity.ok(responseBody);
        } catch (Exception exception) {
            logger.error("loginUser : error while logging in user - {}", exception.getMessage());
            throw exception;
        }

    }

    // Update my user data
    @PatchMapping("")
    public ResponseEntity<Object> updateMyData(@Valid @RequestBody Map<String, Object> updates, HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

            UserResponseBody updateUser = userService.updateUser(user, updates);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User details updated successfully.");
            response.put("user", updateUser);

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("updateMyData : error while updating user data - {}", exception.getMessage());
            throw exception;
        }

    }

    // Update any user data - admin
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody Map<String, Object> updates, @PathVariable int userId, HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);
            if (!user.getRole().equals(Role.ADMIN)) {
                logger.error("updateUser : this api can only be accessed by ADMIN");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }

            User userTobeUpdated = userService.retrieveUserById(userId);

            UserResponseBody updatedUser = userService.updateUser(userTobeUpdated, updates);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User details updated successfully.");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("updateUser : error while updating user data - {}", exception.getMessage());
            throw exception;
        }

    }

    // Delete user by id
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(HttpServletRequest request,@PathVariable int userId) {
        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);
            if (!user.getRole().equals(Role.ADMIN)) {
                logger.error("updateUser : this api can only be accessed by ADMIN");
                throw new BaseException(HttpStatus.UNAUTHORIZED, "Authentication Error : Cannot be accessed");
            }

            userService.deleteUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User account deleted successfully.");

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("deleteUser : error while deleting user data - {}", exception.getMessage());
            throw exception;
        }

    }

    // Delete my account

    @DeleteMapping("")
    public ResponseEntity<Object> deleteUser(HttpServletRequest request) {
        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

            userService.deleteUser(user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User account deleted successfully.");

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("deleteUser : error while deleting user data - {}", exception.getMessage());
            throw exception;
        }

    }

    // Get my addresses
    @GetMapping("/addresses")
    public ResponseEntity<Object> retrieveAddressesForUser(HttpServletRequest request) {
        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

            List<Address> userAddresses = userService.retrieveAddressesForUser(user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Addresses fetched successfully");
            response.put("addresses", userAddresses);

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("retrieveAddressesForUser : error while fetching user addresses - {}", exception.getMessage());
            throw exception;
        }
    }

    // Add address
    @PostMapping("/addresses")
    public ResponseEntity<Object> createAddressForUser(@Valid @RequestBody Address address, HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

            Address savedAddress = userService.createAddressForUser(user.getId(), address);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Address added successfully");
            response.put("address", savedAddress);


            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception exception) {
            logger.error("createAddressForUser : error while creating address - {}", exception.getMessage());
            throw exception;
        }

    }

    // Delete Address
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Object> deleteAddressForUser(@PathVariable int addressId, HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);


            List<Address> userAddresses = userService.retrieveAddressesForUser(user.getId());

            boolean addressFound = false;
            for (Address address : userAddresses) {
                if (address.getId() == addressId) {
                    userService.deleteAddressById(addressId);
                    addressFound = true;
                }
            }

            if (!addressFound) {
                logger.error("delete address for user - error while fetching addresses : No address exists with the id : {}", addressId);
                throw new BaseException(HttpStatus.NOT_FOUND, "Address not found");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Address deleted successfully");


            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("delete address for user : error while deleting address - {}", exception.getMessage());
            throw exception;
        }


    }

    // Get address by id
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<Object> retrieveAddressById(@PathVariable int addressId) {
        try {
            Optional<Address> addressOptional = userService.retrieveAddressById(addressId);

            if (addressOptional.isEmpty()) {
                logger.error("retrieveAddressById - error while fetching addresses : No address exists with the id : {}", addressId);
                throw new BaseException(HttpStatus.NOT_FOUND, "Address not found");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Address fetched successfully");
            response.put("address", addressOptional.get());

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("retrieveAddressById - error while fetching addresses : {}", exception.getMessage());
            throw exception;
        }

    }

    // get address string
    @GetMapping("/addresses/{addressId}/string")
    public ResponseEntity<Object> retrieveAddressStringById(@PathVariable int addressId) {
        try {
            String addressString = userService.retrieveAddressStringById(addressId);


            Map<String, Object> response = new HashMap<>();
            response.put("addressDetails", addressString);

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("retrieveAddressStringById - error while fetching addresses : {}", exception.getMessage());
            throw exception;
        }

    }


    // Get my payment methods
    @GetMapping("/paymentMethods")
    public ResponseEntity<Object> retrievePaymentMethodsForUser(HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

            List<PaymentMethod> userPaymentMethods = userService.retrievePaymentMethodsForUser(user.getId());


            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment methods fetched successfully");
            response.put("paymentMethods", userPaymentMethods);


            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("retrievePaymentMethodsForUser - error while fetching payment methods : {}", exception.getMessage());
            throw exception;
        }

    }

    // Add payment method
    @PostMapping("/paymentMethods")
    public ResponseEntity<Object> createPaymentMethodForUser(@Valid @RequestBody PaymentMethod paymentMethod, HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

            PaymentMethod savedPaymentMethod = userService.createPaymentMethodForUser(user.getId(), paymentMethod);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment method created successfully");
            response.put("paymentMethod", savedPaymentMethod);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception exception) {
            logger.error("createPaymentMethodsForUser - error while creating payment method : {}", exception.getMessage());
            throw exception;
        }


    }

    // Delete payment method
    @DeleteMapping("/paymentMethods/{paymentMethodId}")
    public ResponseEntity<Object> deletePaymentMethodForUser(@PathVariable int paymentMethodId, HttpServletRequest request) {

        try {
            String token = userService.extractTokenFromRequest(request);
            User user = userService.getUserDataFromToken(token);

            List<PaymentMethod> userPaymentMethods = userService.retrievePaymentMethodsForUser(user.getId());

            boolean paymentMethodFound = false;
            for (PaymentMethod paymentMethod : userPaymentMethods) {
                if (paymentMethod.getId() == paymentMethodId) {
                    userService.deletePaymentMethodById(paymentMethodId);
                    paymentMethodFound = true;
                }
            }

            if (!paymentMethodFound) {
                logger.error("deletePaymentMethodForUser - error while deleting payment method: No payment methods exists with the id : {}", paymentMethodId);
                throw new BaseException(HttpStatus.NOT_FOUND, "No payment method exists with the id : " + paymentMethodId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment method deleted successfully");


            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("deletePaymentMethod - error while deleting payment method : {}", exception.getMessage());
            throw exception;
        }
    }

    // Get payment method by id
    @GetMapping("/paymentMethods/{paymentMethodId}")
    public ResponseEntity<Object> retrievePaymentMethodById(@PathVariable int paymentMethodId) {

        try {
            Optional<PaymentMethod> paymentMethodOptional = userService.retrievePaymentMethodById(paymentMethodId);

            if (paymentMethodOptional.isEmpty()) {
                logger.error("retrievePaymentMethodById - error while fetching payment method : No payment method exists with the id : {}", paymentMethodId);
                throw new BaseException(HttpStatus.NOT_FOUND, "Payment method not found");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment method fetched successfully");
            response.put("paymentMethod", paymentMethodOptional.get());

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("retrievePaymentMethodById - error while fetching payment method :{}", exception.getMessage());
            throw exception;
        }

    }

    // Get payment method by id as a string
    @GetMapping("/paymentMethods/{paymentMethodId}/string")
    public ResponseEntity<Object> retrievePaymentMethodStringById(@PathVariable int paymentMethodId) {

        try {
            String paymentMethodString = userService.retrievePaymentMethodStringById(paymentMethodId);

            Map<String, Object> response = new HashMap<>();
            response.put("paymentDetails", paymentMethodString);

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            logger.error("retrievePaymentMethodById - error while fetching payment method :{}", exception.getMessage());
            throw exception;
        }

    }

}
