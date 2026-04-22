package com.family.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.bill.entity.PaymentMethod;
import com.family.bill.mapper.PaymentMethodMapper;
import com.family.bill.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付方式服务实现类
 * 
 * @author family-bill
 */
@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    
    private final PaymentMethodMapper paymentMethodMapper;
    
    @Override
    @Cacheable(value = "paymentMethod", key = "'all'")
    public List<PaymentMethod> getAllPaymentMethods() {
        LambdaQueryWrapper<PaymentMethod> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentMethod::getStatus, 1);
        wrapper.orderByAsc(PaymentMethod::getSortOrder);
        return paymentMethodMapper.selectList(wrapper);
    }
    
    @Override
    @Cacheable(value = "paymentMethod", key = "'type:' + #type")
    public List<PaymentMethod> getPaymentMethodsByType(Integer type) {
        LambdaQueryWrapper<PaymentMethod> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentMethod::getStatus, 1);
        if (type != null) {
            wrapper.and(w -> w.eq(PaymentMethod::getType, type).or().eq(PaymentMethod::getType, 3));
        }
        wrapper.orderByAsc(PaymentMethod::getSortOrder);
        return paymentMethodMapper.selectList(wrapper);
    }
}


