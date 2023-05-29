package com.example.user.api;

import com.example.user.datatransfer.jwt.JwtRefreshDto;
import com.example.user.datatransfer.userResponces.LoginUserDto;
import com.example.user.datatransfer.userResponces.RegisterUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
public interface UserAuth {
    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterUserDto registrationRequestDto);

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginUserDto authRequest);

    @PostMapping("/token")
    ResponseEntity<?> getNewAccessAndRefreshToken(@RequestBody JwtRefreshDto refreshRequest);
}
