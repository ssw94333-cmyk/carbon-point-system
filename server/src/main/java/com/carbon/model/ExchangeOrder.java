package com.carbon.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兑换订单实体类
 */
@Data
@TableName("exchange_order")
public class ExchangeOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
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
