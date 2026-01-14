package com.carbon.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class BehaviorRuleDTO {
    @NotBlank(message = "行为类型不能为空")
    private String behaviorType;
    
    @NotBlank(message = "行为名称不能为空")
    private String behaviorName;
    
    @NotBlank(message = "单位不能为空")
    private String unit;
    
    @NotNull(message = "每单位减碳量不能为空")
    @Positive(message = "每单位减碳量必须大于0")
    private BigDecimal carbonReductionPerUnit;
    
    @NotNull(message = "每单位积分不能为空")
    @Positive(message = "每单位积分必须大于0")
    private Integer pointsPerUnit;
    
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private Integer maxPointsPerDay;
    
    @NotNull(message = "是否需要证明材料不能为空")
    private Integer needProof;
    
    private Integer sortOrder;
    private String description;
}
