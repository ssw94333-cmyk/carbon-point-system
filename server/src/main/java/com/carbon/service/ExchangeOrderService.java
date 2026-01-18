package com.carbon.service;

import com.carbon.dto.*;
import com.carbon.mapper.ExchangeOrderMapper;
import com.carbon.mapper.PointsChangeRecordMapper;
import com.carbon.mapper.ProductMapper;
import com.carbon.mapper.ShippingAddressMapper;
import com.carbon.mapper.UserMapper;
import com.carbon.model.ExchangeOrder;
import com.carbon.model.PointsChangeRecord;
import com.carbon.model.Product;
import com.carbon.model.ShippingAddress;
import com.carbon.model.User;
import com.carbon.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 兑换订单服务类
 */
@Service
public class ExchangeOrderService {
    
    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PointsChangeRecordMapper pointsChangeRecordMapper;
    
    @Autowired
    private ShippingAddressMapper shippingAddressMapper;
    
    /**
     * 积分兑换商品
     */
    @Transactional(rollbackFor = Exception.class)
    public ExchangeResponseDTO exchangeProduct(ExchangeRequestDTO dto) {
        Long userId = UserContext.getUserId();
        
        // 查询商品信息
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查商品状态
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }
        
        // 检查库存
        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("库存不足");
        }
        
        // 检查限兑数量
        if (product.getExchangeLimit() > 0) {
            Integer exchangedCount = exchangeOrderMapper.countUserExchanges(userId, dto.getProductId());
            if (exchangedCount + dto.getQuantity() > product.getExchangeLimit()) {
                throw new RuntimeException("超过限兑数量，每人最多兑换" + product.getExchangeLimit() + "件");
            }
        }
        
        // 计算所需积分
        Integer totalPoints = product.getPointsRequired() * dto.getQuantity();
        
        // 查询用户积分
        User user = userMapper.selectById(userId);
        if (user.getCurrentPoints() < totalPoints) {
            throw new RuntimeException("积分不足，当前积分：" + user.getCurrentPoints() + "，需要积分：" + totalPoints);
        }
        
        // 如果是实物商品，需要收货地址
        String receiverName = null;
        String receiverPhone = null;
        String receiverAddress = null;
        if ("实物商品".equals(product.getCategory())) {
            if (dto.getAddressId() == null) {
                throw new RuntimeException("实物商品需要选择收货地址");
            }
            ShippingAddress address = shippingAddressMapper.selectById(dto.getAddressId());
            if (address == null || !address.getUserId().equals(userId)) {
                throw new RuntimeException("收货地址不存在");
            }
            receiverName = address.getReceiverName();
            receiverPhone = address.getReceiverPhone();
            receiverAddress = address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress();
        }
        
        // 扣除用户积分
        userMapper.decreasePoints(userId, totalPoints);
        
        // 减少商品库存
        int updated = productMapper.decreaseStock(dto.getProductId(), dto.getQuantity());
        if (updated == 0) {
            throw new RuntimeException("商品库存不足");
        }
        
        // 创建兑换订单
        ExchangeOrder order = new ExchangeOrder();
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(dto.getProductId());
        order.setProductName(product.getName());
        order.setQuantity(dto.getQuantity());
        order.setPointsRequired(product.getPointsRequired());
        order.setTotalPoints(totalPoints);
        order.setOrderStatus(0); // 待处理
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setRemark(dto.getRemark());
        exchangeOrderMapper.insert(order);
        
        // 记录积分变动
        PointsChangeRecord record = new PointsChangeRecord();
        record.setUserId(userId);
        record.setChangeType(2); // 消费
        record.setSourceType("积分兑换");
        record.setSourceId(order.getId());
        record.setPoints(-totalPoints);
        record.setBalanceBefore(user.getCurrentPoints());
        record.setBalanceAfter(user.getCurrentPoints() - totalPoints);
        record.setRemark("兑换商品：" + product.getName());
        pointsChangeRecordMapper.insert(record);
        
        return new ExchangeResponseDTO(order.getId(), orderNo);
    }
    
    /**
     * 获取兑换订单列表
     */
    public PageResult<OrderVO> getOrderList(Integer page, Integer size, Integer orderStatus, String orderNo, Long userId) {
        // 如果不是管理员，只能查询自己的订单
        if (UserContext.getUserType() != 1) {
            userId = UserContext.getUserId();
        }
        
        OrderQueryDTO query = new OrderQueryDTO();
        query.setUserId(userId);
        query.setOrderStatus(orderStatus);
        query.setOrderNo(orderNo);
        
        int offset = (page - 1) * size;
        List<OrderVO> list = exchangeOrderMapper.selectOrderList(query, offset, size);
        Integer total = exchangeOrderMapper.countOrders(query);
        
        return new PageResult<>(list, total.longValue(), page, size);
    }
    
    /**
     * 获取订单详情
     */
    public OrderVO getOrderDetail(Long id) {
        OrderVO order = exchangeOrderMapper.selectOrderById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 检查权限
        Long userId = UserContext.getUserId();
        Integer userType = UserContext.getUserType();
        if (userType != 1 && !order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限查看该订单");
        }
        
        return order;
    }
    
    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        Long userId = UserContext.getUserId();
        
        ExchangeOrder order = exchangeOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 检查权限
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0) {
            throw new RuntimeException("只能取消待处理状态的订单");
        }
        
        // 更新订单状态
        exchangeOrderMapper.updateOrderStatus(id, 3); // 已取消
        
        // 退还积分
        User user = userMapper.selectById(userId);
        userMapper.increasePoints(userId, order.getTotalPoints());
        
        // 恢复商品库存
        productMapper.increaseStock(order.getProductId(), order.getQuantity());
        
        // 记录积分变动
        PointsChangeRecord record = new PointsChangeRecord();
        record.setUserId(userId);
        record.setChangeType(1); // 获得
        record.setSourceType("订单取消");
        record.setSourceId(order.getId());
        record.setPoints(order.getTotalPoints());
        record.setBalanceBefore(user.getCurrentPoints());
        record.setBalanceAfter(user.getCurrentPoints() + order.getTotalPoints());
        record.setRemark("取消订单退还积分：" + order.getProductName());
        pointsChangeRecordMapper.insert(record);
    }
    
    /**
     * 订单发货（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long id, ShipOrderDTO dto) {
        // 检查是否为管理员
        if (UserContext.getUserType() != 1) {
            throw new RuntimeException("无权限操作");
        }
        
        ExchangeOrder order = exchangeOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 0) {
            throw new RuntimeException("只能对待处理状态的订单进行发货");
        }
        
        // 更新发货信息
        exchangeOrderMapper.updateShipInfo(id, dto.getExpressCompany(), dto.getExpressNo());
    }
    
    /**
     * 完成订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long id) {
        Long userId = UserContext.getUserId();
        
        ExchangeOrder order = exchangeOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 检查权限
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        
        // 检查订单状态
        if (order.getOrderStatus() != 1) {
            throw new RuntimeException("只能确认已发货状态的订单");
        }
        
        // 更新订单状态
        exchangeOrderMapper.updateOrderStatus(id, 2); // 已完成
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String random = String.valueOf((int) (Math.random() * 10000));
        return "EO" + timestamp + String.format("%04d", Integer.parseInt(random));
    }
}
