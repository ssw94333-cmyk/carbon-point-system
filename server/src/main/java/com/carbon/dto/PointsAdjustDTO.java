package com.carbon.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PointsAdjustDTO {
    
    @NotNull(message = "调整类型不能为空")
    @Min(value = 1, message = "调整类型错误")
    @Max(value = 2, message = "调整类型错误")
    private Integer adjustType;
    
    @NotNull(message = "调整数量不能为空")
    @Min(value = 1, message = "调整数量最小为1")
    @Max(value = 10000, message = "调整数量最大为10000")
    private Integer points;
    
    @NotBlank(message = "调整原因不能为空")
    @Size(max = 200, message = "调整原因不能超过200个字符")
    private String remark;
}
