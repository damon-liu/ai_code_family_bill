package com.family.bill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * 支付方式实体类
 * 
 * @author family-bill
 */
@Data
@TableName("payment_method")
public class PaymentMethod {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 支付方式名称
     */
    private String name;
    
    /**
     * 图标URL
     */
    private String icon;
    
    /**
     * 类型：1-支出方式，2-收入方式，3-通用
     */
    private Integer type;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}


