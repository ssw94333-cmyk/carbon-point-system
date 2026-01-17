package com.carbon.dto;

import lombok.Data;

import java.util.List;

@Data
public class BehaviorStatisticsVO {
    private Integer totalCount;
    private Double totalCarbon;
    private Integer totalPoints;
    private Double passRate;
    private List<BehaviorTypeStatistics> behaviorTypeStats;
    private List<MonthlyTrend> monthlyTrend;
    
    @Data
    public static class BehaviorTypeStatistics {
        private String behaviorName;
        private String behaviorType;
        private String unit;
        private Integer count;
        private Double totalValue;
        private Double totalCarbon;
        private Integer totalPoints;
        private Double percentage;
    }
    
    @Data
    public static class MonthlyTrend {
        private String month;
        private Integer count;
        private Double carbon;
        private Integer points;
    }
}
