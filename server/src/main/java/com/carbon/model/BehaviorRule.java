package com.carbon.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("behavior_rule")
public class BehaviorRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String behaviorType;
    private String behaviorName;
    private String unit;
    private BigDecimal carbonReductionPerUnit;
    private Integer pointsPerUnit;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private Integer maxPointsPerDay;
    private Integer needProof;
    private Integer status;
    private Integer sortOrder;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
