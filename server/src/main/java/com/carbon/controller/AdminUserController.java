package com.carbon.controller;

import com.carbon.common.Result;
import com.carbon.dto.*;
import com.carbon.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final AdminUserService adminUserService;
    
    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Result<PageResult<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status) {
        
        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setPageSize(pageSize);
        queryDTO.setUsername(username);
        queryDTO.setPhone(phone);
        queryDTO.setUserType(userType);
        queryDTO.setStatus(status);
        
        PageResult<UserVO> result = adminUserService.getUserList(queryDTO);
        return Result.success(result);
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<UserVO> getUserDetail(@PathVariable Long id) {
        UserVO userVO = adminUserService.getUserDetail(id);
        if (userVO == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userVO);
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        Integer status = request.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态参数错误");
        }
        
        adminUserService.updateUserStatus(id, status);
        return Result.success();
    }
    
    /**
     * 编辑用户信息
     */
    @PutMapping("/{id}")
    public Result<Void> editUser(
            @PathVariable Long id,
            @RequestBody @Validated UserUpdateDTO updateDTO) {
        
        adminUserService.editUser(id, updateDTO);
        return Result.success();
    }
    
    /**
     * 调整用户积分
     */
    @PutMapping("/{id}/points")
    public Result<Void> adjustUserPoints(
            @PathVariable Long id,
            @RequestBody @Validated PointsAdjustDTO adjustDTO) {
        
        adminUserService.adjustUserPoints(id, adjustDTO);
        return Result.success();
    }
    
    /**
     * 获取用户统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Integer>> getUserStatistics() {
        Map<String, Integer> statistics = adminUserService.getUserStatistics();
        return Result.success(statistics);
    }
}
