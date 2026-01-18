package com.carbon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 兑换响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponseDTO {
    private Long orderId;
    
    private String orderNo;
}
