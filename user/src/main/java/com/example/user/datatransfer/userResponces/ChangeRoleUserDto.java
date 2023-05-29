package com.example.user.datatransfer.userResponces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class ChangeRoleUserDto {
    private Integer userId;
    private String role;
}
