package com.carbon.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointsInfoVO {
    private Integer currentPoints;
    private Integer totalPoints;
    private BigDecimal totalCarbonReduction;
}
