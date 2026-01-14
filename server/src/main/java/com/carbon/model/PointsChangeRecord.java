package com.carbon.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("points_change_record")
public class PointsChangeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Integer changeType;
    
    private String sourceType;
    
    private Long sourceId;
    
    private Integer points;
    
    private Integer balanceBefore;
    
    private Integer balanceAfter;
    
    private String remark;
    
    private LocalDateTime createTime;
}
