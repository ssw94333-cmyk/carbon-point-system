package com.carbon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carbon.dto.BehaviorRecordVO;
import com.carbon.dto.BehaviorStatisticsVO;
import com.carbon.model.CarbonBehaviorRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CarbonBehaviorRecordMapper extends BaseMapper<CarbonBehaviorRecord> {
    
    IPage<BehaviorRecordVO> selectRecordPage(Page<?> page, 
                                              @Param("userId") Long userId,
                                              @Param("behaviorType") String behaviorType,
                                              @Param("auditStatus") Integer auditStatus,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
    
    BehaviorRecordVO selectRecordById(@Param("id") Long id);
    
    IPage<BehaviorRecordVO> selectAuditPage(Page<?> page,
                                            @Param("userId") Long userId,
                                            @Param("behaviorType") String behaviorType,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);
    
    List<BehaviorStatisticsVO.BehaviorTypeStatistics> selectStatistics(@Param("userId") Long userId,
                                                                        @Param("startTime") LocalDateTime startTime,
                                                                        @Param("endTime") LocalDateTime endTime);
}
