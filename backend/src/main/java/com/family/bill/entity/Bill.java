package com.family.bill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 账单实体类
 * 
 * @author family-bill
 */
@Data
@TableName("bill")
public class Bill {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 收款方
     */
    private String payee;
    
    /**
     * 金额（支出为负数，收入为正数）
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
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}


