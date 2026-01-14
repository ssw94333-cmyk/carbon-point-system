package com.carbon.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class BehaviorRecordDTO {
    @NotBlank(message = "行为类型不能为空")
    private String behaviorType;
    
    @NotNull(message = "行为数值不能为空")
    @Positive(message = "行为数值必须大于0")
    private BigDecimal behaviorValue;
    
    private String description;
    private String proofImage;
}
