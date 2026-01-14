package com.carbon.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
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
    
    private LocalDateTime updateTime;
}
