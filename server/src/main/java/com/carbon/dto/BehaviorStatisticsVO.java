package com.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BehaviorStatisticsVO {
    private Integer totalRecords;
    private BigDecimal totalCarbonReduction;
    private Integer totalPoints;
    private List<BehaviorTypeStatistics> typeStatistics;
    
    @Data
    public static class BehaviorTypeStatistics {
        private String behaviorType;
        private String behaviorName;
        private Integer count;
        private BigDecimal carbonReduction;
        private Integer points;
    }
}
