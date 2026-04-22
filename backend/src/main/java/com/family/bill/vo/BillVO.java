package com.family.bill.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 账单视图对象
 * 
 * @author family-bill
 */
@Data
public class BillVO {
    
    private Long id;
    
    /**
     * 收款方
     */
    private String payee;
    
    /**
     * 金额
     */
    private BigDecimal amount;
    
    /**
     * 交易时间
     */
    private Date transactionTime;
    
    /**
     * 支付方式ID
     */
    private Long paymentMethodId;
    
    /**
     * 支付方式名称
     */
    private String paymentMethodName;
    
    /**
     * 交易号
     */
    private String transactionNo;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
}


