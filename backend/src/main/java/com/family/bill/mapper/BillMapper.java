package com.family.bill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.family.bill.entity.Bill;
import com.family.bill.vo.BillVO;
import com.family.bill.vo.StatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

/**
 * 账单Mapper
 * 
 * @author family-bill
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {
    
    /**
     * 分页查询账单列表（带关联信息）
     */
    IPage<BillVO> selectBillPage(Page<BillVO> page, @Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime,
                                  @Param("paymentMethodId") Long paymentMethodId,
                                  @Param("categoryId") Long categoryId,
                                  @Param("type") Integer type);
    
    /**
     * 统计总收入
     */
    BigDecimal selectTotalIncome(@Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime);
    
    /**
     * 统计总支出
     */
    BigDecimal selectTotalExpense(@Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime);
    
    /**
     * 按分类统计
     */
    List<StatisticsVO.CategoryStatistics> selectCategoryStatistics(@Param("startTime") Date startTime,
                                                                     @Param("endTime") Date endTime);
    
    /**
     * 按支付方式统计
     */
    List<StatisticsVO.PaymentMethodStatistics> selectPaymentMethodStatistics(@Param("startTime") Date startTime,
                                                                              @Param("endTime") Date endTime);
    
    /**
     * 根据ID查询账单详情（带关联信息）
     */
    BillVO selectBillById(@Param("id") Long id);
}

