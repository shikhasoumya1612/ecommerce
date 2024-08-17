package org.example.cartservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    private static String PRODUCT_SERVICE_URL = "http://localhost:8060/products" ;

    @Bean
    public WebClient webClient() {
        return WebClient.create(PRODUCT_SERVICE_URL);
    }
}
