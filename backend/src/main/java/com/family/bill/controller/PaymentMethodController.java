package com.family.bill.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.bill.common.Result;
import com.family.bill.dto.PaymentMethodQueryDTO;
import com.family.bill.entity.PaymentMethod;
import com.family.bill.service.PaymentMethodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付方式控制器
 *
 * @author family-bill
 */
@Api(tags = "支付方式管理")
@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @ApiOperation("查询所有支付方式")
    @GetMapping
    public Result<List<PaymentMethod>> getAllPaymentMethods(@RequestParam(required = false) Integer type) {
        List<PaymentMethod> list;
        if (type != null) {
            list = paymentMethodService.getPaymentMethodsByType(type);
        } else {
            list = paymentMethodService.getAllPaymentMethods();
        }
        return Result.success(list);
    }

    @ApiOperation("分页查询支付方式")
    @GetMapping("/page")
    public Result<IPage<PaymentMethod>> getPaymentMethodPage(@Validated PaymentMethodQueryDTO queryDTO) {
        IPage<PaymentMethod> page = paymentMethodService.getPaymentMethodPage(queryDTO);
        return Result.success(page);
    }
}
