package com.carbon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.carbon.dto.AddressDTO;
import com.carbon.mapper.ShippingAddressMapper;
import com.carbon.model.ShippingAddress;
import com.carbon.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShippingAddressService {
    
    @Autowired
    private ShippingAddressMapper shippingAddressMapper;
    
    /**
     * 获取收货地址列表
     */
    public List<ShippingAddress> getAddressList() {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<ShippingAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShippingAddress::getUserId, userId);
        wrapper.orderByDesc(ShippingAddress::getIsDefault);
        wrapper.orderByDesc(ShippingAddress::getCreateTime);
        return shippingAddressMapper.selectList(wrapper);
    }
    
    /**
     * 添加收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(AddressDTO dto) {
        Long userId = UserContext.getUserId();
        
        // 如果设置为默认地址，先取消其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            cancelOtherDefaultAddress(userId);
        }
        
        ShippingAddress address = new ShippingAddress();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);
        
        shippingAddressMapper.insert(address);
    }
    
    /**
     * 更新收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long id, AddressDTO dto) {
        Long userId = UserContext.getUserId();
        
        ShippingAddress address = shippingAddressMapper.selectById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        
        // 如果设置为默认地址，先取消其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            cancelOtherDefaultAddress(userId);
        }
        
        BeanUtils.copyProperties(dto, address);
        shippingAddressMapper.updateById(address);
    }
    
    /**
     * 删除收货地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long id) {
        Long userId = UserContext.getUserId();
        
        ShippingAddress address = shippingAddressMapper.selectById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        
        boolean isDefault = address.getIsDefault() == 1;
        shippingAddressMapper.deleteById(id);
        
        // 如果删除的是默认地址，自动设置第一个地址为默认
        if (isDefault) {
            LambdaQueryWrapper<ShippingAddress> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShippingAddress::getUserId, userId);
            wrapper.orderByDesc(ShippingAddress::getCreateTime);
            wrapper.last("LIMIT 1");
            ShippingAddress firstAddress = shippingAddressMapper.selectOne(wrapper);
            if (firstAddress != null) {
                firstAddress.setIsDefault(1);
                shippingAddressMapper.updateById(firstAddress);
            }
        }
    }
    
    /**
     * 设置默认地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long id) {
        Long userId = UserContext.getUserId();
        
        ShippingAddress address = shippingAddressMapper.selectById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        
        // 取消其他默认地址
        cancelOtherDefaultAddress(userId);
        
        // 设置为默认地址
        address.setIsDefault(1);
        shippingAddressMapper.updateById(address);
    }
    
    /**
     * 取消其他默认地址
     */
    private void cancelOtherDefaultAddress(Long userId) {
        LambdaUpdateWrapper<ShippingAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ShippingAddress::getUserId, userId);
        wrapper.eq(ShippingAddress::getIsDefault, 1);
        wrapper.set(ShippingAddress::getIsDefault, 0);
        shippingAddressMapper.update(null, wrapper);
    }
}
