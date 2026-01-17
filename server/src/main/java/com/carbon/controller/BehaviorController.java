package com.carbon.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carbon.common.Result;
import com.carbon.dto.*;
import com.carbon.model.BehaviorRule;
import com.carbon.service.BehaviorService;
import com.carbon.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/behavior")
@RequiredArgsConstructor
public class BehaviorController {
    
    private final BehaviorService behaviorService;
    
    /**
     * 获取行为规则列表
     */
    @GetMapping("/rules")
    public Result<List<BehaviorRule>> getRuleList() {
        List<BehaviorRule> rules = behaviorService.getRuleList();
        return Result.success(rules);
    }
    
    /**
     * 获取行为规则详情
     */
    @GetMapping("/rules/{id}")
    public Result<BehaviorRule> getRuleById(@PathVariable Long id) {
        BehaviorRule rule = behaviorService.getRuleById(id);
        return Result.success(rule);
    }
    
    /**
     * 提交行为记录
     */
    @PostMapping("/record")
    public Result<Void> submitRecord(@Validated @RequestBody BehaviorRecordDTO dto) {
        Long userId = UserContext.getUserId();
        behaviorService.submitRecord(userId, dto);
        return Result.success();
    }
    
    /**
     * 获取行为记录列表
     */
    @GetMapping("/record")
    public Result<IPage<BehaviorRecordVO>> getRecordPage(
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        IPage<BehaviorRecordVO> page = behaviorService.getRecordPage(
                userId, behaviorType, auditStatus, startTime, endTime, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取行为记录详情
     */
    @GetMapping("/record/{id}")
    public Result<BehaviorRecordVO> getRecordById(@PathVariable Long id) {
        BehaviorRecordVO record = behaviorService.getRecordById(id);
        return Result.success(record);
    }
    
    /**
     * 删除行为记录
     */
    @DeleteMapping("/record/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        behaviorService.deleteRecord(userId, id);
        return Result.success();
    }
    
    /**
     * 获取行为统计数据
     */
    @GetMapping("/statistics")
    public Result<BehaviorStatisticsVO> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String endTime) {
        Long userId = UserContext.getUserId();
        
        // 将日期字符串转换为LocalDateTime
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startTime != null && !startTime.isEmpty()) {
            startDateTime = LocalDate.parse(startTime).atStartOfDay();
        }
        
        if (endTime != null && !endTime.isEmpty()) {
            endDateTime = LocalDate.parse(endTime).atTime(23, 59, 59);
        }
        
        BehaviorStatisticsVO statistics = behaviorService.getStatistics(userId, startDateTime, endDateTime);
        return Result.success(statistics);
    }
    
    /**
     * 获取待审核行为记录列表（管理员）
     */
    @GetMapping("/audit/list")
    public Result<IPage<BehaviorRecordVO>> getAuditPage(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BehaviorRecordVO> page = behaviorService.getAuditPage(
                userId, behaviorType, startTime, endTime, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 审核行为记录（管理员）
     */
    @PutMapping("/audit/{id}")
    public Result<Void> auditRecord(@PathVariable Long id,
                                     @Validated @RequestBody BehaviorAuditDTO dto) {
        Long auditUserId = UserContext.getUserId();
        behaviorService.auditRecord(auditUserId, id, dto);
        return Result.success();
    }
    
    /**
     * 创建行为规则（管理员）
     */
    @PostMapping("/rules")
    public Result<Void> createRule(@Validated @RequestBody BehaviorRuleDTO dto) {
        behaviorService.createRule(dto);
        return Result.success();
    }
    
    /**
     * 更新行为规则（管理员）
     */
    @PutMapping("/rules/{id}")
    public Result<Void> updateRule(@PathVariable Long id,
                                    @Validated @RequestBody BehaviorRuleDTO dto) {
        behaviorService.updateRule(id, dto);
        return Result.success();
    }
    
    /**
     * 删除行为规则（管理员）
     */
    @DeleteMapping("/rules/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        behaviorService.deleteRule(id);
        return Result.success();
    }
    
    /**
     * 获取全局统计数据（管理员）
     */
    @GetMapping("/admin/statistics")
    public Result<Map<String, Object>> getAdminStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> statistics = behaviorService.getAdminStatistics(startDate, endDate);
        return Result.success(statistics);
    }
    
    /**
     * 获取管理员端行为统计数据（所有用户）
     */
    @GetMapping("/admin/behavior-statistics")
    public Result<BehaviorStatisticsVO> getAdminBehaviorStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String endTime) {
        
        // 将日期字符串转换为LocalDateTime
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startTime != null && !startTime.isEmpty()) {
            startDateTime = LocalDate.parse(startTime).atStartOfDay();
        }
        
        if (endTime != null && !endTime.isEmpty()) {
            endDateTime = LocalDate.parse(endTime).atTime(23, 59, 59);
        }
        
        BehaviorStatisticsVO statistics = behaviorService.getAdminBehaviorStatistics(startDateTime, endDateTime);
        return Result.success(statistics);
    }
}
