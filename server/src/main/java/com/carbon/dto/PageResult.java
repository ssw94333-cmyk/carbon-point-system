package com.carbon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Integer page;
    private Integer size;
    private RecordStatistics statistics;
    
    // 为了兼容旧的前端代码，同时提供list字段
    @JsonProperty("list")
    public List<T> getList() {
        return records;
    }
    
    public PageResult(List<T> records, Long total) {
        this.records = records;
        this.total = total;
    }
    
    public PageResult(List<T> records, Long total, Integer page, Integer size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}
