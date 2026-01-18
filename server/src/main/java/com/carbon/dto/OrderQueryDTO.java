package com.carbon.dto;

import lombok.Data;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO {
    private Long userId;
    
    private Integer orderStatus;
    
    private String startTime;
    
    private String endTime;
    
    private String orderNo;
}
