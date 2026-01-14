package com.carbon.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class BehaviorAuditDTO {
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;
    
    private String auditRemark;
}
