package com.family.bill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.bill.entity.PaymentMethod;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付方式Mapper
 * 
 * @author family-bill
 */
@Mapper
public interface PaymentMethodMapper extends BaseMapper<PaymentMethod> {
}


