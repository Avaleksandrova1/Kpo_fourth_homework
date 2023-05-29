package com.example.user.controller;

import com.example.user.api.UserAuth;
import com.example.user.datatransfer.jwt.JwtRefreshDto;
import com.example.user.datatransfer.userResponces.LoginUserDto;
import com.example.user.datatransfer.userResponces.RegisterUserDto;
import com.example.user.exceptions.InvalidException;
import com.example.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController implements UserAuth {
    public final AuthService authService;


    @Override
    public ResponseEntity<?> register(RegisterUserDto registrationRequestDto) {
        try {
            return ResponseEntity.ok(authService.register(registrationRequestDto));
        } catch (InvalidException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> login(LoginUserDto authRequest) {
        try {
            return ResponseEntity.ok(authService.login(authRequest));
        } catch (InvalidException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getNewAccessAndRefreshToken(JwtRefreshDto refreshRequest) {
        try {
            return ResponseEntity.ok(authService.getAccessToken(refreshRequest.getRefreshToken()));
        } catch (InvalidException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }
}
