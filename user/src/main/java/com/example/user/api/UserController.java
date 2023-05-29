package com.example.user.api;

import com.example.user.datatransfer.userResponces.ChangeRoleUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
public interface UserController {
    @GetMapping("/get_info")
    ResponseEntity<?> checkAccess(@RequestHeader("Authorization") String accessToken);

    @GetMapping("/get_user_list")
    ResponseEntity<?> getUserList(@RequestHeader("Authorization") String accessToken);

    @PostMapping("/change_role")
    ResponseEntity<?> changeRoleById(@RequestHeader("Authorization") String accessToken,
                                     @RequestBody ChangeRoleUserDto userDto);
}
