package com.carbon.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.carbon.common.Result;
import com.carbon.dto.*;
import com.carbon.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        try {
            userService.register(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody UserLoginDTO dto) {
        try {
            LoginResponseVO response = userService.login(dto);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        try {
            UserInfoVO userInfo = userService.getCurrentUserInfo();
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody UserUpdateDTO dto) {
        try {
            userService.updateUserInfo(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        try {
            userService.updatePassword(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户积分信息
     */
    @GetMapping("/points")
    public Result<PointsInfoVO> getUserPoints() {
        try {
            PointsInfoVO pointsInfo = userService.getUserPoints();
            return Result.success(pointsInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 管理员获取用户列表
     */
    @GetMapping("/list")
    public Result<IPage<UserInfoVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status) {
        try {
            IPage<UserInfoVO> userPage = userService.getUserList(page, size, username, phone, userType, status);
            return Result.success(userPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 管理员获取用户详情
     */
    @GetMapping("/{id}")
    public Result<UserInfoVO> getUserDetail(@PathVariable Long id) {
        try {
            UserInfoVO userInfo = userService.getUserDetail(id);
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 管理员修改用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            userService.updateUserStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 管理员编辑用户信息
     */
    @PutMapping("/{id}")
    public Result<Void> adminUpdateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        try {
            userService.adminUpdateUser(id, dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
