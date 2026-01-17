package com.carbon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carbon.dto.*;
import com.carbon.mapper.BehaviorRuleMapper;
import com.carbon.mapper.CarbonBehaviorRecordMapper;
import com.carbon.mapper.PointsChangeRecordMapper;
import com.carbon.mapper.UserMapper;
import com.carbon.model.BehaviorRule;
import com.carbon.model.CarbonBehaviorRecord;
import com.carbon.model.PointsChangeRecord;
import com.carbon.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class BehaviorService {
    
    private final BehaviorRuleMapper behaviorRuleMapper;
    private final CarbonBehaviorRecordMapper carbonBehaviorRecordMapper;
    private final UserMapper userMapper;
    private final PointsChangeRecordMapper pointsChangeRecordMapper;
    
    /**
     * 获取行为规则列表
     */
    public List<BehaviorRule> getRuleList() {
        LambdaQueryWrapper<BehaviorRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BehaviorRule::getStatus, 1)
                .orderByAsc(BehaviorRule::getSortOrder);
        return behaviorRuleMapper.selectList(wrapper);
    }
    
    /**
     * 获取行为规则详情
     */
    public BehaviorRule getRuleById(Long id) {
        BehaviorRule rule = behaviorRuleMapper.selectById(id);
        if (rule == null) {
            throw new RuntimeException("行为规则不存在");
        }
        return rule;
    }
    
    /**
     * 提交行为记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitRecord(Long userId, BehaviorRecordDTO dto) {
        log.info("=== 开始提交行为记录 ===");
        log.info("userId: {}, behaviorType: {}, behaviorValue: {}", userId, dto.getBehaviorType(), dto.getBehaviorValue());
        
        // 查询行为规则
        LambdaQueryWrapper<BehaviorRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BehaviorRule::getBehaviorType, dto.getBehaviorType())
                .eq(BehaviorRule::getStatus, 1);
        BehaviorRule rule = behaviorRuleMapper.selectOne(wrapper);
        if (rule == null) {
            log.error("行为规则不存在或已禁用: {}", dto.getBehaviorType());
            throw new RuntimeException("行为规则不存在或已禁用");
        }
        
        log.info("找到行为规则: {}", rule.getBehaviorName());
        
        // 验证行为数值
        if (rule.getMinValue() != null && dto.getBehaviorValue().compareTo(rule.getMinValue()) < 0) {
            throw new RuntimeException("行为数值不能小于" + rule.getMinValue() + rule.getUnit());
        }
        
        // 验证每日最大数值限制
        if (rule.getMaxValue() != null) {
            LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            LambdaQueryWrapper<CarbonBehaviorRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(CarbonBehaviorRecord::getUserId, userId)
                    .eq(CarbonBehaviorRecord::getBehaviorType, dto.getBehaviorType())
                    .between(CarbonBehaviorRecord::getCreateTime, startOfDay, endOfDay);
            List<CarbonBehaviorRecord> todayRecords = carbonBehaviorRecordMapper.selectList(recordWrapper);
            
            BigDecimal todayTotal = todayRecords.stream()
                    .map(CarbonBehaviorRecord::getBehaviorValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (todayTotal.add(dto.getBehaviorValue()).compareTo(rule.getMaxValue()) > 0) {
                throw new RuntimeException("今日该行为数值已达上限");
            }
        }
        
        // 计算减碳量和积分
        BigDecimal carbonReduction = dto.getBehaviorValue()
                .multiply(rule.getCarbonReductionPerUnit())
                .setScale(2, RoundingMode.HALF_UP);
        
        Integer points = dto.getBehaviorValue()
                .multiply(new BigDecimal(rule.getPointsPerUnit()))
                .intValue();
        
        log.info("计算结果 - 减碳量: {}, 积分: {}", carbonReduction, points);
        
        // 验证每日最大积分限制
        if (rule.getMaxPointsPerDay() != null) {
            LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            LambdaQueryWrapper<CarbonBehaviorRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(CarbonBehaviorRecord::getUserId, userId)
                    .eq(CarbonBehaviorRecord::getBehaviorType, dto.getBehaviorType())
                    .between(CarbonBehaviorRecord::getCreateTime, startOfDay, endOfDay);
            List<CarbonBehaviorRecord> todayRecords = carbonBehaviorRecordMapper.selectList(recordWrapper);
            
            Integer todayPoints = todayRecords.stream()
                    .mapToInt(CarbonBehaviorRecord::getPoints)
                    .sum();
            
            log.info("今日已获得积分: {}, 最大限制: {}", todayPoints, rule.getMaxPointsPerDay());
            
            if (todayPoints + points > rule.getMaxPointsPerDay()) {
                points = rule.getMaxPointsPerDay() - todayPoints;
                if (points <= 0) {
                    log.error("今日该行为积分已达上限");
                    throw new RuntimeException("今日该行为积分已达上限");
                }
                log.info("调整后积分: {}", points);
            }
        }
        
        // 验证是否需要证明材料
        log.info("needProof: {}, proofImage: {}", rule.getNeedProof(), dto.getProofImage());
        if (rule.getNeedProof() == 1 && (dto.getProofImage() == null || dto.getProofImage().trim().isEmpty())) {
            log.error("该行为需要上传证明材料，但未提供");
            throw new RuntimeException("该行为需要上传证明材料");
        }
        
        log.info("开始保存行为记录...");
        
        // 保存行为记录
        CarbonBehaviorRecord record = new CarbonBehaviorRecord();
        record.setUserId(userId);
        record.setBehaviorType(dto.getBehaviorType());
        record.setBehaviorName(rule.getBehaviorName());
        record.setBehaviorValue(dto.getBehaviorValue());
        record.setCarbonReduction(carbonReduction);
        record.setPoints(points);
        record.setDescription(dto.getDescription());
        record.setProofImage(dto.getProofImage());
        record.setAuditStatus(0);
        
        carbonBehaviorRecordMapper.insert(record);
        log.info("行为记录保存成功，ID: {}", record.getId());
        log.info("=== 行为记录提交完成 ===");
    }
    
    /**
     * 获取行为记录列表
     */
    public IPage<BehaviorRecordVO> getRecordPage(Long userId, String behaviorType, Integer auditStatus,
                                                  LocalDateTime startTime, LocalDateTime endTime,
                                                  Integer pageNum, Integer pageSize) {
        log.info("=== 查询行为记录列表 ===");
        log.info("userId: {}, behaviorType: {}, auditStatus: {}, pageNum: {}, pageSize: {}", 
                userId, behaviorType, auditStatus, pageNum, pageSize);
        
        Page<BehaviorRecordVO> page = new Page<>(pageNum, pageSize);
        IPage<BehaviorRecordVO> result = carbonBehaviorRecordMapper.selectRecordPage(page, userId, behaviorType, auditStatus, startTime, endTime);
        
        log.info("查询结果 - 总记录数: {}, 当前页记录数: {}", result.getTotal(), result.getRecords().size());
        
        return result;
    }
    
    /**
     * 获取行为记录详情
     */
    public BehaviorRecordVO getRecordById(Long id) {
        BehaviorRecordVO record = carbonBehaviorRecordMapper.selectRecordById(id);
        if (record == null) {
            throw new RuntimeException("行为记录不存在");
        }
        return record;
    }
    
    /**
     * 删除行为记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecord(Long userId, Long id) {
        CarbonBehaviorRecord record = carbonBehaviorRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("行为记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该记录");
        }
        if (record.getAuditStatus() != 0) {
            throw new RuntimeException("只能删除待审核状态的记录");
        }
        carbonBehaviorRecordMapper.deleteById(id);
    }
    
    /**
     * 获取行为统计数据
     */
    public BehaviorStatisticsVO getStatistics(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("=== 查询行为统计数据 ===");
        log.info("userId: {}, startTime: {}, endTime: {}", userId, startTime, endTime);
        
        BehaviorStatisticsVO vo = new BehaviorStatisticsVO();
        
        // 1. 查询各类行为统计
        List<BehaviorStatisticsVO.BehaviorTypeStatistics> typeStats = 
                carbonBehaviorRecordMapper.selectStatistics(userId, startTime, endTime);
        vo.setBehaviorTypeStats(typeStats);
        
        // 2. 查询月度趋势
        List<BehaviorStatisticsVO.MonthlyTrend> monthlyTrend = 
                carbonBehaviorRecordMapper.selectMonthlyTrend(userId, startTime, endTime);
        vo.setMonthlyTrend(monthlyTrend);
        
        // 3. 计算总计
        Integer totalCount = typeStats.stream()
                .mapToInt(BehaviorStatisticsVO.BehaviorTypeStatistics::getCount)
                .sum();
        
        Double totalCarbon = typeStats.stream()
                .mapToDouble(BehaviorStatisticsVO.BehaviorTypeStatistics::getTotalCarbon)
                .sum();
        
        Integer totalPoints = typeStats.stream()
                .mapToInt(BehaviorStatisticsVO.BehaviorTypeStatistics::getTotalPoints)
                .sum();
        
        vo.setTotalCount(totalCount);
        vo.setTotalCarbon(Math.round(totalCarbon * 100.0) / 100.0);
        vo.setTotalPoints(totalPoints);
        
        // 4. 计算审核通过率
        Integer totalRecords = carbonBehaviorRecordMapper.countByUserId(userId, startTime, endTime);
        Integer passedRecords = carbonBehaviorRecordMapper.countByUserIdAndStatus(userId, 1, startTime, endTime);
        Double passRate = totalRecords > 0 ? (passedRecords * 100.0 / totalRecords) : 0.0;
        vo.setPassRate(Math.round(passRate * 100.0) / 100.0);
        
        log.info("统计结果 - 总次数: {}, 总减碳: {}, 总积分: {}, 通过率: {}%", 
                totalCount, totalCarbon, totalPoints, passRate);
        
        return vo;
    }
    
    /**
     * 获取待审核行为记录列表
     */
    public IPage<BehaviorRecordVO> getAuditPage(Long userId, String behaviorType,
                                                 LocalDateTime startTime, LocalDateTime endTime,
                                                 Integer pageNum, Integer pageSize) {
        Page<BehaviorRecordVO> page = new Page<>(pageNum, pageSize);
        return carbonBehaviorRecordMapper.selectAuditPage(page, userId, behaviorType, startTime, endTime);
    }
    
    /**
     * 审核行为记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditRecord(Long auditUserId, Long id, BehaviorAuditDTO dto) {
        CarbonBehaviorRecord record = carbonBehaviorRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("行为记录不存在");
        }
        if (record.getAuditStatus() != 0) {
            throw new RuntimeException("该记录已审核");
        }
        
        // 更新审核信息
        record.setAuditStatus(dto.getAuditStatus());
        record.setAuditUserId(auditUserId);
        record.setAuditTime(LocalDateTime.now());
        record.setAuditRemark(dto.getAuditRemark());
        carbonBehaviorRecordMapper.updateById(record);
        
        // 审核通过，发放积分并更新用户数据
        if (dto.getAuditStatus() == 1) {
            User user = userMapper.selectById(record.getUserId());
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            Integer balanceBefore = user.getCurrentPoints();
            Integer balanceAfter = balanceBefore + record.getPoints();
            
            // 更新用户积分和减碳量
            user.setTotalPoints(user.getTotalPoints() + record.getPoints());
            user.setCurrentPoints(balanceAfter);
            user.setTotalCarbonReduction(user.getTotalCarbonReduction().add(record.getCarbonReduction()));
            userMapper.updateById(user);
            
            // 记录积分变动
            PointsChangeRecord pointsRecord = new PointsChangeRecord();
            pointsRecord.setUserId(record.getUserId());
            pointsRecord.setChangeType(1);
            pointsRecord.setSourceType("碳行为");
            pointsRecord.setSourceId(record.getId());
            pointsRecord.setPoints(record.getPoints());
            pointsRecord.setBalanceBefore(balanceBefore);
            pointsRecord.setBalanceAfter(balanceAfter);
            pointsRecord.setRemark(record.getBehaviorName() + "获得积分");
            pointsChangeRecordMapper.insert(pointsRecord);
        }
    }
    
    /**
     * 创建行为规则
     */
    @Transactional(rollbackFor = Exception.class)
    public void createRule(BehaviorRuleDTO dto) {
        // 验证行为类型唯一性
        LambdaQueryWrapper<BehaviorRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BehaviorRule::getBehaviorType, dto.getBehaviorType());
        if (behaviorRuleMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该行为类型已存在");
        }
        
        BehaviorRule rule = new BehaviorRule();
        rule.setBehaviorType(dto.getBehaviorType());
        rule.setBehaviorName(dto.getBehaviorName());
        rule.setUnit(dto.getUnit());
        rule.setCarbonReductionPerUnit(dto.getCarbonReductionPerUnit());
        rule.setPointsPerUnit(dto.getPointsPerUnit());
        rule.setMinValue(dto.getMinValue());
        rule.setMaxValue(dto.getMaxValue());
        rule.setMaxPointsPerDay(dto.getMaxPointsPerDay());
        rule.setNeedProof(dto.getNeedProof());
        rule.setStatus(1);
        rule.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        rule.setDescription(dto.getDescription());
        
        behaviorRuleMapper.insert(rule);
    }
    
    /**
     * 更新行为规则
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(Long id, BehaviorRuleDTO dto) {
        BehaviorRule rule = behaviorRuleMapper.selectById(id);
        if (rule == null) {
            throw new RuntimeException("行为规则不存在");
        }
        
        // 如果修改了行为类型，验证唯一性
        if (!rule.getBehaviorType().equals(dto.getBehaviorType())) {
            LambdaQueryWrapper<BehaviorRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BehaviorRule::getBehaviorType, dto.getBehaviorType());
            if (behaviorRuleMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("该行为类型已存在");
            }
        }
        
        rule.setBehaviorType(dto.getBehaviorType());
        rule.setBehaviorName(dto.getBehaviorName());
        rule.setUnit(dto.getUnit());
        rule.setCarbonReductionPerUnit(dto.getCarbonReductionPerUnit());
        rule.setPointsPerUnit(dto.getPointsPerUnit());
        rule.setMinValue(dto.getMinValue());
        rule.setMaxValue(dto.getMaxValue());
        rule.setMaxPointsPerDay(dto.getMaxPointsPerDay());
        rule.setNeedProof(dto.getNeedProof());
        rule.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : rule.getSortOrder());
        rule.setDescription(dto.getDescription());
        
        behaviorRuleMapper.updateById(rule);
    }
    
    /**
     * 删除行为规则
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRule(Long id) {
        BehaviorRule rule = behaviorRuleMapper.selectById(id);
        if (rule == null) {
            throw new RuntimeException("行为规则不存在");
        }
        
        // 检查是否有使用该规则的记录
        LambdaQueryWrapper<CarbonBehaviorRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarbonBehaviorRecord::getBehaviorType, rule.getBehaviorType());
        if (carbonBehaviorRecordMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该规则已被使用，不允许删除");
        }
        
        behaviorRuleMapper.deleteById(id);
    }
    
    /**
     * 获取所有行为记录（管理端）
     */
    public Map<String, Object> getAdminRecords(String username, String behaviorType, Integer auditStatus,
                                                LocalDateTime startTime, LocalDateTime endTime,
                                                Integer pageNum, Integer pageSize) {
        log.info("=== 查询管理端行为记录 ===");
        log.info("username: {}, behaviorType: {}, auditStatus: {}, pageNum: {}, pageSize: {}", 
                username, behaviorType, auditStatus, pageNum, pageSize);
        
        Map<String, Object> result = new java.util.HashMap<>();
        
        // 查询记录列表
        Page<BehaviorRecordVO> page = new Page<>(pageNum, pageSize);
        IPage<BehaviorRecordVO> recordPage = carbonBehaviorRecordMapper.selectAdminRecordPage(
                page, username, behaviorType, auditStatus, startTime, endTime);
        
        // 查询统计信息
        RecordStatistics statistics = carbonBehaviorRecordMapper.selectRecordStatistics(
                username, behaviorType, auditStatus, startTime, endTime);
        
        result.put("list", recordPage.getRecords());
        result.put("total", recordPage.getTotal());
        result.put("statistics", statistics);
        
        log.info("查询结果 - 总记录数: {}, 当前页记录数: {}, 待审核: {}, 已通过: {}, 已拒绝: {}", 
                recordPage.getTotal(), recordPage.getRecords().size(),
                statistics.getPendingCount(), statistics.getPassedCount(), statistics.getRejectedCount());
        
        return result;
    }
    
    /**
     * 获取全局统计数据（管理员）
     */
    public Map<String, Object> getAdminStatistics(String startDate, String endDate) {
        log.info("=== 查询管理员统计数据 ===");
        log.info("startDate: {}, endDate: {}", startDate, endDate);
        
        // 转换日期参数
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if (startDate != null && !startDate.isEmpty()) {
            startTime = LocalDate.parse(startDate).atStartOfDay();
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            endTime = LocalDate.parse(endDate).atTime(23, 59, 59);
        }
        
        Map<String, Object> result = new java.util.HashMap<>();
        
        // 1. 用户总数
        result.put("totalUsers", userMapper.countTotalUsers());
        
        // 2. 活跃用户数（最近30天）
        result.put("activeUsers", carbonBehaviorRecordMapper.countActiveUsers(30));
        
        // 3. 今日新增用户
        result.put("todayNewUsers", userMapper.countTodayNewUsers());
        
        // 4. 本月新增用户
        result.put("monthNewUsers", userMapper.countMonthNewUsers());
        
        // 5. 累计发放积分
        result.put("totalPointsIssued", carbonBehaviorRecordMapper.sumApprovedPoints());
        
        // 6. 累计消费积分（暂时返回0，等积分商城功能开发后再实现）
        result.put("totalPointsConsumed", 0);
        
        // 7. 累计减碳量
        BigDecimal carbonReduction = carbonBehaviorRecordMapper.sumCarbonReduction();
        result.put("totalCarbonReduction", carbonReduction != null ? carbonReduction.doubleValue() : 0.0);
        
        // 8. 待审核行为数
        result.put("pendingBehaviors", carbonBehaviorRecordMapper.countPendingBehaviors());
        
        log.info("管理员统计结果 - 用户总数: {}, 活跃用户: {}, 待审核: {}", 
                result.get("totalUsers"), result.get("activeUsers"), result.get("pendingBehaviors"));
        
        return result;
    }
    
    /**
     * 获取管理员行为记录列表（新接口）
     */
    public PageResult<BehaviorRecordVO> getAdminRecordList(BehaviorRecordQueryDTO queryDTO) {
        log.info("=== 查询管理员行为记录列表 ===");
        log.info("username: {}, behaviorType: {}, auditStatus: {}, page: {}, pageSize: {}", 
                queryDTO.getUsername(), queryDTO.getBehaviorType(), queryDTO.getAuditStatus(), 
                queryDTO.getPage(), queryDTO.getPageSize());
        
        // 转换日期参数
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
            startTime = LocalDate.parse(queryDTO.getStartTime()).atStartOfDay();
        }
        
        if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
            endTime = LocalDate.parse(queryDTO.getEndTime()).atTime(23, 59, 59);
        }
        
        // 查询记录列表
        Page<BehaviorRecordVO> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<BehaviorRecordVO> recordPage = carbonBehaviorRecordMapper.selectAdminRecordPage(
                page, queryDTO.getUsername(), queryDTO.getBehaviorType(), 
                queryDTO.getAuditStatus(), startTime, endTime);
        
        // 查询统计信息
        RecordStatistics statistics = carbonBehaviorRecordMapper.selectRecordStatistics(
                queryDTO.getUsername(), queryDTO.getBehaviorType(), 
                queryDTO.getAuditStatus(), startTime, endTime);
        
        // 组装返回结果
        PageResult<BehaviorRecordVO> result = new PageResult<>();
        result.setList(recordPage.getRecords());
        result.setTotal(recordPage.getTotal());
        result.setStatistics(statistics);
        
        log.info("查询结果 - 总记录数: {}, 当前页记录数: {}, 待审核: {}, 已通过: {}, 已拒绝: {}", 
                recordPage.getTotal(), recordPage.getRecords().size(),
                statistics.getPendingCount(), statistics.getPassedCount(), statistics.getRejectedCount());
        
        return result;
    }
    
    /**
     * 获取管理员端行为统计数据（所有用户）
     */
    public BehaviorStatisticsVO getAdminBehaviorStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("=== 查询管理员端行为统计数据（所有用户） ===");
        log.info("startTime: {}, endTime: {}", startTime, endTime);
        
        BehaviorStatisticsVO vo = new BehaviorStatisticsVO();
        
        // 1. 查询各类行为统计（所有用户）
        List<BehaviorStatisticsVO.BehaviorTypeStatistics> typeStats = 
                carbonBehaviorRecordMapper.selectAdminStatistics(startTime, endTime);
        vo.setBehaviorTypeStats(typeStats);
        
        // 2. 查询月度趋势（所有用户）
        List<BehaviorStatisticsVO.MonthlyTrend> monthlyTrend = 
                carbonBehaviorRecordMapper.selectAdminMonthlyTrend(startTime, endTime);
        vo.setMonthlyTrend(monthlyTrend);
        
        // 3. 计算总计
        Integer totalCount = typeStats.stream()
                .mapToInt(BehaviorStatisticsVO.BehaviorTypeStatistics::getCount)
                .sum();
        
        Double totalCarbon = typeStats.stream()
                .mapToDouble(BehaviorStatisticsVO.BehaviorTypeStatistics::getTotalCarbon)
                .sum();
        
        Integer totalPoints = typeStats.stream()
                .mapToInt(BehaviorStatisticsVO.BehaviorTypeStatistics::getTotalPoints)
                .sum();
        
        vo.setTotalCount(totalCount);
        vo.setTotalCarbon(Math.round(totalCarbon * 100.0) / 100.0);
        vo.setTotalPoints(totalPoints);
        
        // 4. 计算审核通过率（所有用户）
        Integer totalRecords = carbonBehaviorRecordMapper.countAllRecords(startTime, endTime);
        Integer passedRecords = carbonBehaviorRecordMapper.countAllRecordsByStatus(1, startTime, endTime);
        Double passRate = totalRecords > 0 ? (passedRecords * 100.0 / totalRecords) : 0.0;
        vo.setPassRate(Math.round(passRate * 100.0) / 100.0);
        
        log.info("管理员统计结果 - 总次数: {}, 总减碳: {}, 总积分: {}, 通过率: {}%", 
                totalCount, totalCarbon, totalPoints, passRate);
        
        return vo;
    }
}
