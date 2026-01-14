package com.carbon.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String nickname;
    
    private String avatar;
    
    private String email;
    
    private String phone;
}
