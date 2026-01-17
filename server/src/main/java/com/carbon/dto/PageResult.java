package com.carbon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;
    private Long total;
    private RecordStatistics statistics;
    
    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }
}
