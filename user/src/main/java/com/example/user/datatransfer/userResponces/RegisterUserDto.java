package com.example.user.datatransfer.userResponces;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RegisterUserDto {
    private String username;
    private String email;
    private String password;
}
