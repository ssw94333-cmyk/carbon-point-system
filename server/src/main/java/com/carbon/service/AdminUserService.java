package com.carbon.service;

import com.carbon.dto.*;
import com.carbon.mapper.PointsChangeRecordMapper;
import com.carbon.mapper.UserMapper;
import com.carbon.model.PointsChangeRecord;
import com.carbon.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    
    private final UserMapper userMapper;
    private final PointsChangeRecordMapper pointsChangeRecordMapper;
    
    /**
     * 获取用户列表
     */
    public PageResult<UserVO> getUserList(UserQueryDTO queryDTO) {
        // 计算偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        
        // 查询用户列表
        List<UserVO> list = userMapper.selectUserList(queryDTO, offset);
        
        // 查询总数
        Integer total = userMapper.countUsers(queryDTO);
        
        return new PageResult<>(list, total.longValue());
    }
    
    /**
     * 获取用户详情
     */
    public UserVO getUserDetail(Long id) {
        return userMapper.selectUserById(id);
    }
    
    /**
     * 更新用户状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        // 检查用户是否存在
        UserVO user = userMapper.selectUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 不能禁用自己
        Long currentUserId = UserContext.getUserId();
        if (id.equals(currentUserId)) {
            throw new RuntimeException("不能禁用自己的账号");
        }
        
        // 更新状态
        userMapper.updateStatus(id, status);
    }
    
    /**
     * 编辑用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUser(Long id, UserUpdateDTO updateDTO) {
        // 检查用户是否存在
        UserVO user = userMapper.selectUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 如果修改了邮箱，检查邮箱是否已被使用
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isEmpty()) {
            if (user.getEmail() == null || !updateDTO.getEmail().equals(user.getEmail())) {
                UserVO existUser = userMapper.selectByEmail(updateDTO.getEmail());
                if (existUser != null && !existUser.getId().equals(id)) {
                    throw new RuntimeException("该邮箱已被使用");
                }
            }
        }
        
        // 更新用户信息
        userMapper.updateUserInfo(id, updateDTO.getNickname(), updateDTO.getEmail());
    }
    
    /**
     * 调整用户积分
     */
    @Transactional(rollbackFor = Exception.class)
    public void adjustUserPoints(Long id, PointsAdjustDTO adjustDTO) {
        // 检查用户是否存在
        UserVO user = userMapper.selectUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Integer points = adjustDTO.getPoints();
        Integer adjustType = adjustDTO.getAdjustType();
        Long operatorId = UserContext.getUserId();
        
        Integer balanceBefore = user.getCurrentPoints();
        Integer balanceAfter;
        
        if (adjustType == 1) {
            // 增加积分
            userMapper.increasePoints(id, points);
            balanceAfter = balanceBefore + points;
        } else if (adjustType == 2) {
            // 减少积分 - 检查积分是否足够
            if (user.getCurrentPoints() < points) {
                throw new RuntimeException("用户积分不足");
            }
            userMapper.decreasePoints(id, points);
            balanceAfter = balanceBefore - points;
        } else {
            throw new RuntimeException("调整类型参数错误");
        }
        
        // 记录积分变动历史
        PointsChangeRecord record = new PointsChangeRecord();
        record.setUserId(id);
        record.setChangeType(adjustType);
        record.setSourceType("管理员调整");
        record.setPoints(points);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setRemark(adjustDTO.getRemark());
        pointsChangeRecordMapper.insert(record);
    }
    
    /**
     * 获取用户统计数据
     */
    public Map<String, Integer> getUserStatistics() {
        Map<String, Integer> result = new HashMap<>();
        
        // 用户总数
        result.put("totalUsers", userMapper.countTotalUsers());
        
        // 今日新增用户
        result.put("todayNewUsers", userMapper.countTodayNewUsers());
        
        // 本月新增用户
        result.put("monthNewUsers", userMapper.countMonthNewUsers());
        
        // 按用户类型统计
        result.put("normalUsers", userMapper.countByUserType(0));
        result.put("adminUsers", userMapper.countByUserType(1));
        
        // 按状态统计
        result.put("enabledUsers", userMapper.countByStatus(1));
        result.put("disabledUsers", userMapper.countByStatus(0));
        
        return result;
    }
}
