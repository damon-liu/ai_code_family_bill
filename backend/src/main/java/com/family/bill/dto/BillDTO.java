package com.family.bill.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 账单DTO
 * 
 * @author family-bill
 */
@Data
public class BillDTO {
    
    private Long id;
    
    /**
     * 收款方
     */
    @NotBlank(message = "收款方不能为空")
    private String payee;
    
    /**
     * 金额（支出为负数，收入为正数）
     */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
    
    /**
     * 交易时间
     */
    @NotNull(message = "交易时间不能为空")
    private Date transactionTime;
    
    /**
     * 支付方式ID
     */
    @NotNull(message = "支付方式不能为空")
    private Long paymentMethodId;
    
    /**
     * 交易号
     */
    private String transactionNo;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 备注
     */
    private String remark;
}


