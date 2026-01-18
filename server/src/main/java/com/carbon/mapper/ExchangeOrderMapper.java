package com.carbon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carbon.dto.OrderQueryDTO;
import com.carbon.dto.OrderVO;
import com.carbon.model.ExchangeOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 兑换订单Mapper接口
 */
@Mapper
public interface ExchangeOrderMapper extends BaseMapper<ExchangeOrder> {
    
    /**
     * 查询订单列表
     */
    List<OrderVO> selectOrderList(@Param("query") OrderQueryDTO query, @Param("offset") Integer offset, @Param("size") Integer size);
    
    /**
     * 统计订单数量
     */
    Integer countOrders(@Param("query") OrderQueryDTO query);
    
    /**
     * 根据ID查询订单详情
     */
    OrderVO selectOrderById(@Param("id") Long id);
    
    /**
     * 更新订单状态
     */
    @Update("UPDATE exchange_order SET order_status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateOrderStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 更新订单发货信息
     */
    @Update("UPDATE exchange_order SET express_company = #{expressCompany}, express_no = #{expressNo}, order_status = 1, update_time = NOW() WHERE id = #{id}")
    void updateShipInfo(@Param("id") Long id, @Param("expressCompany") String expressCompany, @Param("expressNo") String expressNo);
    
    /**
     * 统计用户兑换某商品的次数
     */
    @Select("SELECT COUNT(*) FROM exchange_order WHERE user_id = #{userId} AND product_id = #{productId} AND order_status != 3")
    Integer countUserExchanges(@Param("userId") Long userId, @Param("productId") Long productId);
}
