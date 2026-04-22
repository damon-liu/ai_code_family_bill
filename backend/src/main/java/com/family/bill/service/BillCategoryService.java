package com.family.bill.service;

import com.family.bill.entity.BillCategory;

import java.util.List;

/**
 * 账单分类服务接口
 * 
 * @author family-bill
 */
public interface BillCategoryService {
    
    /**
     * 查询所有分类
     */
    List<BillCategory> getAllCategories();
    
    /**
     * 根据类型查询分类
     */
    List<BillCategory> getCategoriesByType(Integer type);
}


