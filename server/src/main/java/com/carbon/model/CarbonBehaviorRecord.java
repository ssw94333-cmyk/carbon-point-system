package com.carbon.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("carbon_behavior_record")
public class CarbonBehaviorRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String behaviorType;
    private String behaviorName;
    private BigDecimal behaviorValue;
    private BigDecimal carbonReduction;
    private Integer points;
    private String description;
    private String proofImage;
    private Integer auditStatus;
    private Long auditUserId;
    private LocalDateTime auditTime;
    private String auditRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
