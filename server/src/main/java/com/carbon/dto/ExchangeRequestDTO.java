package com.carbon.dto;

import lombok.Data;

/**
 * 积分兑换请求DTO
 */
@Data
public class ExchangeRequestDTO {
    private Long productId;
    
    private Integer quantity;
    
    private Long addressId;
    
    private String remark;
}
