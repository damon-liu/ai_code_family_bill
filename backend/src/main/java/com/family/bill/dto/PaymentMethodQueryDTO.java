package com.family.bill.dto;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 支付方式分页查询DTO
 *
 * @author family-bill
 */
@Data
public class PaymentMethodQueryDTO {

    /**
     * 支付方式名称（模糊查询）
     */
    private String name;

    /**
     * 类型：1-支出方式，2-收入方式，3-通用
     */
    private Integer type;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 页码，默认1
     */
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    /**
     * 每页大小，默认10
     */
    @Min(value = 1, message = "每页大小最小为1")
    private Integer pageSize = 10;
}
