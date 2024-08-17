package org.example.orderservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    private static String PRODUCT_SERVICE_URL = "http://localhost:8060/products" ;
    private static String USER_SERVICE_URL = "http://localhost:8080/users" ;

    @Bean
    public WebClient productClient() {
        return WebClient.create(PRODUCT_SERVICE_URL);
    }

    @Bean
    public WebClient userClient() {
        return WebClient.create(USER_SERVICE_URL);
    }
}
