package com.carbon.dto;

import lombok.Data;

@Data
public class BehaviorRecordQueryDTO {
    private Integer page;
    private Integer pageSize;
    private String username;
    private String behaviorType;
    private Integer auditStatus;
    private String startTime;
    private String endTime;
}
