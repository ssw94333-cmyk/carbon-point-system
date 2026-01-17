package com.carbon.dto;

import lombok.Data;

@Data
public class RecordStatistics {
    private Integer totalCount;
    private Integer pendingCount;
    private Integer passedCount;
    private Integer rejectedCount;
}
