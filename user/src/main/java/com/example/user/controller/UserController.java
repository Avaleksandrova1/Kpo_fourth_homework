package com.example.user.controller;

import com.example.user.domain.entity.Role;
import com.example.user.datatransfer.userResponces.ChangeRoleUserDto;
import com.example.user.exceptions.UserException;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements com.example.user.api.UserController {
    public final UserService userService;

    @Override
    public ResponseEntity<?> checkAccess(String accessToken) {
        try {
            return ResponseEntity.ok(userService.getUserInfo(accessToken));
        } catch (UserException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getUserList(String accessToken) {
        try {
            return ResponseEntity.ok(userService.getUserList(accessToken));
        } catch (UserException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> changeRoleById(String accessToken, ChangeRoleUserDto userDto) {
        try {
            return ResponseEntity.ok(userService.changeUserRole(
                    accessToken,
                    userDto.getUserId(),
                    Role.valueOf(userDto.getRole())));
        } catch (UserException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        }
    }
}
