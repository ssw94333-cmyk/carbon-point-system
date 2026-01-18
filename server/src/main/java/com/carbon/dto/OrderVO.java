package com.carbon.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单视图对象
 */
@Data
public class OrderVO {
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private String username;
    
    private Long productId;
    
    private String productName;
    
    private Integer quantity;
    
    private Integer pointsRequired;
    
    private Integer totalPoints;
    
    private Integer orderStatus;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverAddress;
    
    private String expressCompany;
    
    private String expressNo;
    
    private String remark;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
