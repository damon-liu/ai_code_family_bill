package com.family.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.bill.entity.BillCategory;
import com.family.bill.mapper.BillCategoryMapper;
import com.family.bill.service.BillCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账单分类服务实现类
 * 
 * @author family-bill
 */
@Service
@RequiredArgsConstructor
public class BillCategoryServiceImpl implements BillCategoryService {
    
    private final BillCategoryMapper billCategoryMapper;
    
    @Override
    @Cacheable(value = "category", key = "'all'")
    public List<BillCategory> getAllCategories() {
        LambdaQueryWrapper<BillCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BillCategory::getStatus, 1);
        wrapper.orderByAsc(BillCategory::getSortOrder);
        return billCategoryMapper.selectList(wrapper);
    }
    
    @Override
    @Cacheable(value = "category", key = "'type:' + #type")
    public List<BillCategory> getCategoriesByType(Integer type) {
        LambdaQueryWrapper<BillCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BillCategory::getStatus, 1);
        if (type != null) {
            wrapper.eq(BillCategory::getType, type);
        }
        wrapper.orderByAsc(BillCategory::getSortOrder);
        return billCategoryMapper.selectList(wrapper);
    }
}


