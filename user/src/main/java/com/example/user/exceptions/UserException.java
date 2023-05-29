package com.example.user.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true)
public class UserException extends Exception {
    private HttpStatus httpStatus;
    private String message;
}
