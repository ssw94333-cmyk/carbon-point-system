package com.carbon.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carbon.common.Result;
import com.carbon.dto.*;
import com.carbon.model.BehaviorRule;
import com.carbon.service.BehaviorService;
import com.carbon.util.FileUploadUtil;
import com.carbon.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Long userId = UserContext.getUserId();
        BehaviorStatisticsVO statistics = behaviorService.getStatistics(userId, startTime, endTime);
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
     * 上传行为证明材料
     */
    @PostMapping("/upload/behavior-proof")
    public Result<String> uploadProof(@RequestParam("file") MultipartFile file) {
        try {
            String url = FileUploadUtil.uploadFile(file, "behavior-proof");
            return Result.success("上传成功", url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
