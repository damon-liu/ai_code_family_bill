package com.family.bill.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.bill.common.Result;
import com.family.bill.dto.BillDTO;
import com.family.bill.dto.BillQueryDTO;
import com.family.bill.service.BillService;
import com.family.bill.vo.BillVO;
import com.family.bill.vo.StatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 账单控制器
 * 
 * @author family-bill
 */
@Api(tags = "账单管理")
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {
    
    private final BillService billService;
    
    @ApiOperation("分页查询账单列表")
    @GetMapping
    public Result<IPage<BillVO>> getBillPage(@Validated BillQueryDTO queryDTO) {
        IPage<BillVO> page = billService.getBillPage(queryDTO);
        return Result.success(page);
    }
    
    @ApiOperation("根据ID查询账单")
    @GetMapping("/{id}")
    public Result<BillVO> getBillById(@PathVariable Long id) {
        BillVO billVO = billService.getBillById(id);
        return Result.success(billVO);
    }
    
    @ApiOperation("新增账单")
    @PostMapping
    public Result<Long> createBill(@RequestBody @Validated BillDTO billDTO) {
        Long id = billService.createBill(billDTO);
        return Result.success("创建成功", id);
    }
    
    @ApiOperation("更新账单")
    @PutMapping("/{id}")
    public Result<String> updateBill(@PathVariable Long id, @RequestBody @Validated BillDTO billDTO) {
        billService.updateBill(id, billDTO);
        return Result.success("更新成功");
    }
    
    @ApiOperation("删除账单")
    @DeleteMapping("/{id}")
    public Result<String> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return Result.success("删除成功");
    }
    
    @ApiOperation("获取统计信息")
    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics(@Validated BillQueryDTO queryDTO) {
        StatisticsVO statistics = billService.getStatistics(queryDTO);
        return Result.success(statistics);
    }
}

