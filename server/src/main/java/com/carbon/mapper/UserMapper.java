package com.carbon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carbon.dto.UserQueryDTO;
import com.carbon.dto.UserVO;
import com.carbon.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM user")
    Integer countTotalUsers();
    
    /**
     * 统计今日新增用户
     */
    @Select("SELECT COUNT(*) FROM user WHERE DATE(create_time) = CURDATE()")
    Integer countTodayNewUsers();
    
    /**
     * 统计本月新增用户
     */
    @Select("SELECT COUNT(*) FROM user WHERE YEAR(create_time) = YEAR(NOW()) AND MONTH(create_time) = MONTH(NOW())")
    Integer countMonthNewUsers();
    
    /**
     * 按用户类型统计
     */
    @Select("SELECT COUNT(*) FROM user WHERE user_type = #{userType}")
    Integer countByUserType(@Param("userType") Integer userType);
    
    /**
     * 按状态统计
     */
    @Select("SELECT COUNT(*) FROM user WHERE status = #{status}")
    Integer countByStatus(@Param("status") Integer status);
    
    /**
     * 查询用户列表（分页）
     */
    List<UserVO> selectUserList(@Param("query") UserQueryDTO query, @Param("offset") Integer offset);
    
    /**
     * 统计用户数量（用于分页）
     */
    Integer countUsers(@Param("query") UserQueryDTO query);
    
    /**
     * 根据ID查询用户详情
     */
    UserVO selectUserById(@Param("id") Long id);
    
    /**
     * 根据邮箱查询用户
     */
    UserVO selectByEmail(@Param("email") String email);
    
    /**
     * 更新用户状态
     */
    @Update("UPDATE user SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 更新用户信息
     */
    void updateUserInfo(@Param("id") Long id, @Param("nickname") String nickname, @Param("email") String email);
    
    /**
     * 增加用户积分
     */
    @Update("UPDATE user SET current_points = current_points + #{points}, total_points = total_points + #{points}, update_time = NOW() WHERE id = #{id}")
    void increasePoints(@Param("id") Long id, @Param("points") Integer points);
    
    /**
     * 减少用户积分
     */
    @Update("UPDATE user SET current_points = current_points - #{points}, update_time = NOW() WHERE id = #{id}")
    void decreasePoints(@Param("id") Long id, @Param("points") Integer points);
}
