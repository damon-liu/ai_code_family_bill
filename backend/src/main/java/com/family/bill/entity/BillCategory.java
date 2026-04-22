package com.family.bill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 账单分类实体类
 * 
 * @author family-bill
 */
@Data
@TableName("bill_category")
public class BillCategory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 图标URL
     */
    private String icon;
    
    /**
     * 类型：1-支出，2-收入
     */
    private Integer type;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
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


