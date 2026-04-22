package com.family.bill.controller;

import com.family.bill.common.Result;
import com.family.bill.entity.BillCategory;
import com.family.bill.service.BillCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账单分类控制器
 * 
 * @author family-bill
 */
@Api(tags = "账单分类管理")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class BillCategoryController {
    
    private final BillCategoryService billCategoryService;
    
    @ApiOperation("查询所有分类")
    @GetMapping
    public Result<List<BillCategory>> getAllCategories(@RequestParam(required = false) Integer type) {
        List<BillCategory> list;
        if (type != null) {
            list = billCategoryService.getCategoriesByType(type);
        } else {
            list = billCategoryService.getAllCategories();
        }
        return Result.success(list);
    }
}

