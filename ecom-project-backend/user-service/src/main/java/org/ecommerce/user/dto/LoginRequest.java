package org.ecommerce.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @Email(message = "Invalid email format : Please provide a valid email address.")
    @NotBlank(message="Email field cannot be empty")
    private String email;

    @NotBlank(message="Password field cannot be empty")
    private String password;

    public LoginRequest(String email,String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
