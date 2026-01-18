package com.carbon.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String category;
    
    private String description;
    
    private String image;
    
    private Integer pointsRequired;
    
    private Integer stock;
    
    private Integer totalStock;
    
    private Integer exchangeLimit;
    
    private Integer status;
    
    private Integer sortOrder;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
