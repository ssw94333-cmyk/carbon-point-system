package com.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String phone;
    private String nickname;
    private String avatar;
    private String email;
    private Integer userType;
    private Integer status;
    private Integer totalPoints;
    private Integer currentPoints;
    private BigDecimal totalCarbonReduction;
    private LocalDateTime createTime;
}
