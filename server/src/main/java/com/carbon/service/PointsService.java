package com.carbon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carbon.dto.PageResult;
import com.carbon.mapper.PointsChangeRecordMapper;
import com.carbon.model.PointsChangeRecord;
import com.carbon.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointsService {
    
    @Autowired
    private PointsChangeRecordMapper pointsChangeRecordMapper;
    
    /**
     * 获取积分变动历史记录
     */
    public PageResult<PointsChangeRecord> getPointsHistory(Integer page, Integer size, Integer changeType, String startTime, String endTime) {
        Long userId = UserContext.getUserId();
        
        Page<PointsChangeRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PointsChangeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsChangeRecord::getUserId, userId);
        
        if (changeType != null) {
            wrapper.eq(PointsChangeRecord::getChangeType, changeType);
        }
        
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(PointsChangeRecord::getCreateTime, startTime);
        }
        
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(PointsChangeRecord::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(PointsChangeRecord::getCreateTime);
        
        IPage<PointsChangeRecord> result = pointsChangeRecordMapper.selectPage(pageParam, wrapper);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }
}
