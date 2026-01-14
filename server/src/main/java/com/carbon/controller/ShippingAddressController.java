package com.carbon.controller;

import com.carbon.common.Result;
import com.carbon.dto.AddressDTO;
import com.carbon.model.ShippingAddress;
import com.carbon.service.ShippingAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/address")
public class ShippingAddressController {
    
    @Autowired
    private ShippingAddressService shippingAddressService;
    
    /**
     * 获取收货地址列表
     */
    @GetMapping
    public Result<List<ShippingAddress>> getAddressList() {
        try {
            List<ShippingAddress> addressList = shippingAddressService.getAddressList();
            return Result.success(addressList);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 添加收货地址
     */
    @PostMapping
    public Result<Void> addAddress(@Valid @RequestBody AddressDTO dto) {
        try {
            shippingAddressService.addAddress(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新收货地址
     */
    @PutMapping("/{id}")
    public Result<Void> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDTO dto) {
        try {
            shippingAddressService.updateAddress(id, dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除收货地址
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        try {
            shippingAddressService.deleteAddress(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 设置默认地址
     */
    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        try {
            shippingAddressService.setDefaultAddress(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
