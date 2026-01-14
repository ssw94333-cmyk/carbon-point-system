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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
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
        // 查询行为规则
        LambdaQueryWrapper<BehaviorRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BehaviorRule::getBehaviorType, dto.getBehaviorType())
                .eq(BehaviorRule::getStatus, 1);
        BehaviorRule rule = behaviorRuleMapper.selectOne(wrapper);
        if (rule == null) {
            throw new RuntimeException("行为规则不存在或已禁用");
        }
        
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
            
            if (todayPoints + points > rule.getMaxPointsPerDay()) {
                points = rule.getMaxPointsPerDay() - todayPoints;
                if (points <= 0) {
                    throw new RuntimeException("今日该行为积分已达上限");
                }
            }
        }
        
        // 验证是否需要证明材料
        if (rule.getNeedProof() == 1 && (dto.getProofImage() == null || dto.getProofImage().isEmpty())) {
            throw new RuntimeException("该行为需要上传证明材料");
        }
        
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
    }
    
    /**
     * 获取行为记录列表
     */
    public IPage<BehaviorRecordVO> getRecordPage(Long userId, String behaviorType, Integer auditStatus,
                                                  LocalDateTime startTime, LocalDateTime endTime,
                                                  Integer pageNum, Integer pageSize) {
        Page<BehaviorRecordVO> page = new Page<>(pageNum, pageSize);
        return carbonBehaviorRecordMapper.selectRecordPage(page, userId, behaviorType, auditStatus, startTime, endTime);
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
        List<BehaviorStatisticsVO.BehaviorTypeStatistics> typeStatistics = 
                carbonBehaviorRecordMapper.selectStatistics(userId, startTime, endTime);
        
        BehaviorStatisticsVO vo = new BehaviorStatisticsVO();
        vo.setTypeStatistics(typeStatistics);
        
        Integer totalRecords = typeStatistics.stream()
                .mapToInt(BehaviorStatisticsVO.BehaviorTypeStatistics::getCount)
                .sum();
        
        BigDecimal totalCarbonReduction = typeStatistics.stream()
                .map(BehaviorStatisticsVO.BehaviorTypeStatistics::getCarbonReduction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer totalPoints = typeStatistics.stream()
                .mapToInt(BehaviorStatisticsVO.BehaviorTypeStatistics::getPoints)
                .sum();
        
        vo.setTotalRecords(totalRecords);
        vo.setTotalCarbonReduction(totalCarbonReduction);
        vo.setTotalPoints(totalPoints);
        
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
}
