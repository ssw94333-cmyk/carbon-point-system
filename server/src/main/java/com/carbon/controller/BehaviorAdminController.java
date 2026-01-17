package com.carbon.controller;

import com.carbon.common.Result;
import com.carbon.dto.BehaviorRecordQueryDTO;
import com.carbon.dto.BehaviorRecordVO;
import com.carbon.dto.PageResult;
import com.carbon.service.BehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/behavior/admin")
@RequiredArgsConstructor
public class BehaviorAdminController {
    
    private final BehaviorService behaviorService;
    
    /**
     * 获取所有用户的行为记录列表
     */
    @GetMapping("/records")
    public Result<PageResult<BehaviorRecordVO>> getAdminRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        BehaviorRecordQueryDTO queryDTO = new BehaviorRecordQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setPageSize(pageSize);
        queryDTO.setUsername(username);
        queryDTO.setBehaviorType(behaviorType);
        queryDTO.setAuditStatus(auditStatus);
        queryDTO.setStartTime(startTime);
        queryDTO.setEndTime(endTime);
        
        PageResult<BehaviorRecordVO> result = behaviorService.getAdminRecordList(queryDTO);
        return Result.success(result);
    }
}
