package org.ecommerce.user.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.ecommerce.user.dto.LoginRequest;
import org.ecommerce.user.dto.UserResponseBody;
import org.ecommerce.user.entities.Address;
import org.ecommerce.user.entities.PaymentMethod;
import org.ecommerce.user.entities.Role;
import org.ecommerce.user.entities.User;
import org.ecommerce.user.exception.customExceptions.BaseException;
import org.ecommerce.user.repository.AddressRepository;
import org.ecommerce.user.repository.PaymentMethodRepository;
import org.ecommerce.user.repository.UserRepository;
import org.ecommerce.user.utils.JwtTokenUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(Logger.class);
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private final PaymentMethodRepository paymentMethodRepository;


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


    // get user data from token
    public User getUserDataFromToken(String authToken) {
        try {
            int userId = JwtTokenUtil.getUserIdFromToken(authToken);

            return retrieveUserById(userId);
        } catch (Exception exception) {
            logger.error("getUserDataFromToken : error while extracting user data from token - {}", exception.getMessage());
            throw exception;
        }
    }


    // validate password
    public void validatePassword(String enteredPassword) {
        try {
            if (!enteredPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                if (enteredPassword.length() < 8) {

                    logger.error("validatePassword : error while validating password - Authentication token not present in cookie");
                    throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid Password : The length of the password must be at least 8");

                }

                if (!enteredPassword.matches("^(?=.*[0-9]).*$")) {
                    logger.error("validatePassword : error while validating password - The password must contain at least one digit");
                    throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid Password : The password must contain at least one digit.");

                }
                if (!enteredPassword.matches("^(?=.*[a-z]).*$")) {
                    logger.error("validatePassword : error while validating password - The password must contain at least one lowercase letter");
                    throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid Password : The password must contain at least one lowercase letter.");


                }
                if (!enteredPassword.matches("^(?=.*[A-Z]).*$")) {
                    logger.error("validatePassword : error while validating password - The password must contain at least one uppercase letter");
                    throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid Password : The password must contain at least one uppercase letter.");


                }
                if (!enteredPassword.matches("^(?=.*[@#$%^&+=]).*$")) {
                    logger.error("validatePassword : error while validating password - The password must contain at least one special character");
                    throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid Password : The password must contain at least one special character.");

                }
                if (enteredPassword.matches("^\\S*$")) {
                    logger.error("validatePassword : error while validating password - The password must not contain any whitespace");
                    throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid Password : The password must not contain any whitespace.");

                }
            }
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("validatePassword : error while validating password - {}", exception.getMessage());
            throw exception;
        }

    }


    public UserService(UserRepository userRepository, AddressRepository addressRepository, PaymentMethodRepository paymentMethodRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    // get all users data
    public List<UserResponseBody> retrieveAllUsers() {
        try {
            List<User> fetchedUsers = userRepository.findByRole(Role.CUSTOMER);
            List<UserResponseBody> users = new ArrayList<>();

            for (User userData : fetchedUsers) {
                UserResponseBody filteredUser = new UserResponseBody(userData.getEmail(), userData.getRole(), userData.getName(), userData.getId(), userData.getAddresses(),userData.getImgLink(),userData.getPaymentMethods());

                users.add(filteredUser);
            }
            return users;
        } catch (Exception exception) {
            logger.error("retrieve all users - error while fetching all users : {}", exception.getMessage());
            throw exception;
        }
    }

    // get user by id
    public User retrieveUserById(int userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                logger.error("retrieve user by id - error while fetching user: {}", "user not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "User not found");
            }

            return user.get();
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("retrieve user by id - error while fetching user: {}", exception.getMessage());
            throw exception;
        }
    }

    // register user
    public User createUser(User user) {
        try {
            validatePassword(user.getPassword());

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.setImgLink("https://i.ibb.co/n3tCTrK/avatar-17.png");

            return userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            logger.error("create user - error while creating user : {}", exception.getMessage());
            throw new BaseException(HttpStatus.BAD_REQUEST, "User already exists with this email");

        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("create user - error while creating user : {}", exception.getMessage());
            throw exception;
        }
    }

    // login user
    public User loginUser(LoginRequest requestBody) {
        try {
            Optional<User> user = userRepository.findByEmail(requestBody.getEmail());

            if (user.isEmpty()) {
                logger.error("loginUser - error while logging user : {}", "user not found");
                throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid Credentials");
            }

            if (!BCrypt.checkpw(requestBody.getPassword(), user.get().getPassword())) {
                logger.error("loginUser - error while logging user : {}", "wrong password entered");

                throw new BaseException(HttpStatus.BAD_REQUEST, "Invalid Credentials");
            }

            return user.get();

        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("loginUser - error while logging user : {}", exception.getMessage());
            throw exception;
        }
    }

    // update user
    public UserResponseBody updateUser(User user, Map<String, Object> updates) {
        try {
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String field = entry.getKey();
                Object value = entry.getValue();

                switch (field) {
                    case "name": {
                        user.setName((String) value);
                        break;
                    }
                    case "password": {
                        validatePassword((String) value);
                        String hashedPassword = BCrypt.hashpw((String) value, BCrypt.gensalt());
                        user.setPassword(hashedPassword);
                        break;
                    }
                    case "email": {
                        if (value.equals(user.getEmail())) {
                            break;
                        }

                        Optional<User> existingUser = userRepository.findByEmail((String) value);
                        if (existingUser.isPresent()) {
                            logger.error("update user - error while updating user : {}", "email already exists");
                            throw new BaseException(HttpStatus.BAD_REQUEST, "User already exists with this email");
                        }
                        user.setEmail((String) value);
                        break;
                    }


                    case "imgLink": {
                        user.setImgLink((String) value);
                        break;
                    }
                    default: {
                        logger.error("update user - error while updating user : {}", "these fields cannot be updated");
                        throw new BaseException(HttpStatus.BAD_REQUEST, "Unknown fields sent to update");
                    }
                }
            }

            userRepository.save(user);

            return new UserResponseBody(user.getEmail(), user.getRole(), user.getName(), user.getId(), user.getAddresses(),user.getImgLink(),user.getPaymentMethods());

        } catch (TransactionSystemException exception) {
            logger.error("update user - error while updating user : {}", exception.getMessage());
            throw new BaseException(HttpStatus.BAD_REQUEST, "Please enter a valid email");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("update user - error while updating user : {}", exception.getMessage());
            throw exception;
        }
    }


    // delete user
    public void deleteUser(int userId) {
        try {
            Optional<User> existingUser = userRepository.findById(userId);

            if (existingUser.isEmpty()) {
                logger.error("deleteUser - error while deleting user : {}", "User not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "User not found");
            } else {
                userRepository.deleteById(userId);
            }

        } catch (Exception exception) {
            logger.error("deleteUser - error while deleting user : {}", exception.getMessage());
            throw exception;
        }
    }



    // get addresses for user
    public List<Address> retrieveAddressesForUser(int userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                logger.error("retrieveAddressesForUser - error while fetching user : {}", "No user exists with id : " + userId);
                throw new BaseException(HttpStatus.NOT_FOUND, "User not found");
            }
            return user.get().getAddresses();
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("retrieveAddressesForUser - error while fetching addresses : {}", exception.getMessage());
            throw exception;
        }
    }

    // delete address for user
    public void deleteAddressById(int addressId) {
        try {
            addressRepository.deleteById(addressId);
        } catch (Exception exception) {
            logger.error("deleteAddressById - error while deleting address : {}", exception.getMessage());
            throw exception;
        }
    }

    // add address for user
    public Address createAddressForUser(int userId, Address address) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                logger.error("create address for user - error while fetching user : {}", "No user exists with id : " + userId);
                throw new BaseException(HttpStatus.NOT_FOUND, "User not found");
            }

            // Check if the user already has an address with the same name
            boolean addressExists = addressRepository.existsByUserAndAddressName(user.get(), address.getAddressName());
            if (addressExists) {
                logger.error("create address for user - address with the same name already exists");
                throw new BaseException(HttpStatus.BAD_REQUEST, "Address with the same name already exists");
            }

            address.setUser(user.get());
            return addressRepository.save(address);
        } catch (DataIntegrityViolationException exception) {
            logger.error("create user - error while saving address : {}", exception.getMessage());
            throw new BaseException(HttpStatus.BAD_REQUEST, "Address already exists with this email");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("create address for user - error while saving address : {}", exception.getMessage());
            throw exception;
        }
    }



    // get address by id
    public Optional<Address> retrieveAddressById(int addressId) {
        try {
            Optional<Address> address = addressRepository.findById(addressId);
            if (address.isEmpty()) {
                logger.error("retrieveAddressById - error while fetching address : No address exists with id {}", addressId);
                throw new BaseException(HttpStatus.NOT_FOUND, "Address not found");
            }

            return address;
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("retrieveAddressById - error while fetching address : {} ", exception.getMessage());
            throw exception;
        }
    }


    // payment method


    // get payment methods for user
    public List<PaymentMethod> retrievePaymentMethodsForUser(int userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                logger.error("retrievePaymentMethodsForUser - error while fetching user : No user exists with id {}", userId);

                throw new BaseException(HttpStatus.NOT_FOUND, "User not found");
            }
            return user.get().getPaymentMethods();
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("retrievePaymentMethodsForUser - error while fetching payment methods : {}", exception.getMessage());
            throw exception;
        }
    }

    // delete payment method for user
    public void deletePaymentMethodById(int paymentMethodId) {
        try {
            paymentMethodRepository.deleteById(paymentMethodId);
        } catch (Exception exception) {
            logger.error("deletePaymentMethodsById - error while deleting payment methods : {}", exception.getMessage());
            throw exception;
        }
    }

    // create payment method for user
    public PaymentMethod createPaymentMethodForUser(int userId, PaymentMethod paymentMethod) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                logger.error("createPaymentMethodForUser - error while fetching user : No user exists with id {}", userId);
                throw new BaseException(HttpStatus.NOT_FOUND, "User not found");
            }

            // Check if the user already has an address with the same name
            boolean addressExists = paymentMethodRepository.existsByUserAndAccountId(user.get(), paymentMethod.getAccountId());
            if (addressExists) {
                logger.error("create payment method for user - payment method with the same account id already exists");
                throw new BaseException(HttpStatus.BAD_REQUEST, "Payment method with the same account id already exists");
            }

            paymentMethod.setUser(user.get());
            return paymentMethodRepository.save(paymentMethod);
        } catch (DataIntegrityViolationException exception) {
            logger.error("create user - error while creating payment method : {}", exception.getMessage());
            throw new BaseException(HttpStatus.BAD_REQUEST, "This payment method already exists");
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("createPaymentMethodForUser - error while saving payment method {}", exception.getMessage());
            throw exception;
        }
    }


    // get payment method by id
    public Optional<PaymentMethod> retrievePaymentMethodById(int paymentMethodId) {
        try {
            return paymentMethodRepository.findById(paymentMethodId);
        } catch (Exception exception) {
            logger.error("retrievePaymentMethodById - error while fetching payment method : {}", exception.getMessage());
            throw exception;
        }
    }

    public String retrievePaymentMethodStringById(int paymentMethodId) {
        try {
            Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findById(paymentMethodId);

            if (paymentMethod.isEmpty()) {
                logger.error("retrievePaymentMethodStringById - error while fetching payment method : Payment method not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Payment method not found");
            }

            return paymentMethod.get().toString();

        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("retrievePaymentMethodStringById - error while fetching payment method : {}", exception.getMessage());
            throw exception;
        }
    }

    public String retrieveAddressStringById(int addressId) {
        try {
            Optional<Address> address = addressRepository.findById(addressId);

            if (address.isEmpty()) {
                logger.error("retrieveAddressStringById - error while fetching address : Address not found");
                throw new BaseException(HttpStatus.NOT_FOUND, "Address not found");
            }

            return address.get().toString();

        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("retrieveAddressStringById - error while fetching address : {}", exception.getMessage());
            throw exception;
        }
    }


}
