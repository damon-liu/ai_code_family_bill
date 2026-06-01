package com.family.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.family.bill.dto.PaymentMethodQueryDTO;
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

    /**
     * 分页查询支付方式
     * 支持按名称模糊匹配、类型、状态过滤；按排序字段升序返回
     */
    @Override
    public IPage<PaymentMethod> getPaymentMethodPage(PaymentMethodQueryDTO queryDTO) {
        Page<PaymentMethod> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<PaymentMethod> wrapper = new LambdaQueryWrapper<>();
        // 名称模糊查询（非空才拼接）
        wrapper.like(StringUtils.isNotBlank(queryDTO.getName()),
                PaymentMethod::getName, queryDTO.getName());
        // 类型过滤
        wrapper.eq(queryDTO.getType() != null, PaymentMethod::getType, queryDTO.getType());
        // 状态过滤
        wrapper.eq(queryDTO.getStatus() != null, PaymentMethod::getStatus, queryDTO.getStatus());
        wrapper.orderByAsc(PaymentMethod::getSortOrder);
        return paymentMethodMapper.selectPage(page, wrapper);
    }
}
