package org.example.productservice.exception.customExceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
public class BaseException extends RuntimeException {
    private final HttpStatus status;

    public BaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}


