package com.carbon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carbon.dto.BehaviorRecordVO;
import com.carbon.dto.BehaviorStatisticsVO;
import com.carbon.dto.RecordStatistics;
import com.carbon.model.CarbonBehaviorRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
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
    
    IPage<BehaviorRecordVO> selectAdminRecordPage(Page<?> page,
                                                   @Param("username") String username,
                                                   @Param("behaviorType") String behaviorType,
                                                   @Param("auditStatus") Integer auditStatus,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);
    
    RecordStatistics selectRecordStatistics(@Param("username") String username,
                                           @Param("behaviorType") String behaviorType,
                                           @Param("auditStatus") Integer auditStatus,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);
    
    List<BehaviorStatisticsVO.BehaviorTypeStatistics> selectStatistics(@Param("userId") Long userId,
                                                                        @Param("startTime") LocalDateTime startTime,
                                                                        @Param("endTime") LocalDateTime endTime);
    
    List<BehaviorStatisticsVO.MonthlyTrend> selectMonthlyTrend(@Param("userId") Long userId,
                                                                @Param("startTime") LocalDateTime startTime,
                                                                @Param("endTime") LocalDateTime endTime);
    
    Integer countByUserId(@Param("userId") Long userId,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);
    
    Integer countByUserIdAndStatus(@Param("userId") Long userId,
                                    @Param("auditStatus") Integer auditStatus,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计活跃用户数（最近N天有行为记录的用户）
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM carbon_behavior_record WHERE create_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    Integer countActiveUsers(@Param("days") Integer days);
    
    /**
     * 统计累计发放积分（已审核通过）
     */
    @Select("SELECT COALESCE(SUM(points), 0) FROM carbon_behavior_record WHERE audit_status = 1")
    Integer sumApprovedPoints();
    
    /**
     * 统计累计减碳量（已审核通过）
     */
    @Select("SELECT COALESCE(SUM(carbon_reduction), 0) FROM carbon_behavior_record WHERE audit_status = 1")
    BigDecimal sumCarbonReduction();
    
    /**
     * 统计待审核行为数
     */
    @Select("SELECT COUNT(*) FROM carbon_behavior_record WHERE audit_status = 0")
    Integer countPendingBehaviors();
    
    /**
     * 管理员端：查询所有用户的行为统计（不限用户）
     */
    List<BehaviorStatisticsVO.BehaviorTypeStatistics> selectAdminStatistics(@Param("startTime") LocalDateTime startTime,
                                                                             @Param("endTime") LocalDateTime endTime);
    
    /**
     * 管理员端：查询所有用户的月度趋势（不限用户）
     */
    List<BehaviorStatisticsVO.MonthlyTrend> selectAdminMonthlyTrend(@Param("startTime") LocalDateTime startTime,
                                                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 管理员端：统计所有用户的总记录数
     */
    Integer countAllRecords(@Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 管理员端：统计所有用户指定状态的记录数
     */
    Integer countAllRecordsByStatus(@Param("auditStatus") Integer auditStatus,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
}
