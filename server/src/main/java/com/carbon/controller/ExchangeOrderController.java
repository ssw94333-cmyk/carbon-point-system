package com.carbon.controller;

import com.carbon.common.Result;
import com.carbon.dto.ExchangeRequestDTO;
import com.carbon.dto.ExchangeResponseDTO;
import com.carbon.dto.OrderVO;
import com.carbon.dto.PageResult;
import com.carbon.dto.ShipOrderDTO;
import com.carbon.service.ExchangeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 兑换订单控制器
 */
@RestController
@RequestMapping("/api/order")
public class ExchangeOrderController {
    
    @Autowired
    private ExchangeOrderService exchangeOrderService;
    
    /**
     * 积分兑换商品
     */
    @PostMapping("/exchange")
    public Result<ExchangeResponseDTO> exchangeProduct(@RequestBody ExchangeRequestDTO dto) {
        ExchangeResponseDTO response = exchangeOrderService.exchangeProduct(dto);
        return Result.success("兑换成功", response);
    }
    
    /**
     * 获取兑换订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<OrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId) {
        PageResult<OrderVO> result = exchangeOrderService.getOrderList(page, size, orderStatus, orderNo, userId);
        return Result.success(result);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        OrderVO order = exchangeOrderService.getOrderDetail(id);
        return Result.success(order);
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        exchangeOrderService.cancelOrder(id);
        return Result.success("订单已取消", null);
    }
    
    /**
     * 订单发货（管理员）
     */
    @PutMapping("/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id, @RequestBody ShipOrderDTO dto) {
        exchangeOrderService.shipOrder(id, dto);
        return Result.success("发货成功", null);
    }
    
    /**
     * 完成订单
     */
    @PutMapping("/{id}/complete")
    public Result<Void> completeOrder(@PathVariable Long id) {
        exchangeOrderService.completeOrder(id);
        return Result.success("已确认收货", null);
    }
}
