package com.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BehaviorRecordVO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String behaviorType;
    private String behaviorName;
    private BigDecimal behaviorValue;
    private BigDecimal carbonReduction;
    private Integer points;
    private String description;
    private String proofImage;
    private Integer auditStatus;
    private String auditStatusText;
    private Long auditUserId;
    private String auditUserName;
    private LocalDateTime auditTime;
    private String auditRemark;
    private LocalDateTime createTime;
}
