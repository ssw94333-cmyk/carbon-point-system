package com.carbon.dto;

import lombok.Data;

@Data
public class UserQueryDTO {
    private Integer page;
    private Integer pageSize;
    private String username;
    private String phone;
    private Integer userType;
    private Integer status;
}
