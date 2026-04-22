package com.family.bill.dto;

import lombok.Data;

import java.util.Date;


/**
 * 账单查询DTO
 * 
 * @author family-bill
 */
@Data
public class BillQueryDTO {
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 支付方式ID
     */
    private Long paymentMethodId;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 类型：1-支出，2-收入
     */
    private Integer type;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}


