package com.family.bill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.bill.dto.PaymentMethodQueryDTO;
import com.family.bill.entity.PaymentMethod;

import java.util.List;

/**
 * 支付方式服务接口
 *
 * @author family-bill
 */
public interface PaymentMethodService {

    /**
     * 查询所有支付方式
     */
    List<PaymentMethod> getAllPaymentMethods();

    /**
     * 根据类型查询支付方式
     */
    List<PaymentMethod> getPaymentMethodsByType(Integer type);

    /**
     * 分页查询支付方式
     *
     * @param queryDTO 分页查询参数
     * @return 分页结果
     */
    IPage<PaymentMethod> getPaymentMethodPage(PaymentMethodQueryDTO queryDTO);
}
