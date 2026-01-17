package com.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer userType;
    private Integer status;
    private Integer currentPoints;
    private Integer totalPoints;
    private BigDecimal totalCarbonReduction;
    private LocalDateTime createTime;
}
