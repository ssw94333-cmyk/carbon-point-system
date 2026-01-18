package com.carbon.controller;

import com.carbon.common.Result;
import com.carbon.dto.PageResult;
import com.carbon.model.PointsChangeRecord;
import com.carbon.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/points")
public class PointsController {
    
    @Autowired
    private PointsService pointsService;
    
    /**
     * 获取积分变动历史记录
     */
    @GetMapping("/history")
    public Result<PageResult<PointsChangeRecord>> getPointsHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer changeType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        try {
            PageResult<PointsChangeRecord> historyPage = pointsService.getPointsHistory(page, size, changeType, startTime, endTime);
            return Result.success(historyPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
