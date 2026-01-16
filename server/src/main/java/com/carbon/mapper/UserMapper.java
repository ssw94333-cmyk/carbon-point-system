package com.carbon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carbon.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
