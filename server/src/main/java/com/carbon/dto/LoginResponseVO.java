package com.carbon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseVO {
    private String token;
    private UserInfoVO userInfo;
}
