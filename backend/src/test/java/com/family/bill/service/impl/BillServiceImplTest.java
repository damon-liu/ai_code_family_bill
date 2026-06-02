package com.family.bill.service.impl;

import com.family.bill.dto.BillQueryDTO;
import com.family.bill.mapper.BillMapper;
import com.family.bill.vo.StatisticsVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * BillServiceImpl 单元测试 —— 统计功能
 */
@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    private BillMapper billMapper;

    @InjectMocks
    private BillServiceImpl billService;

    private BillQueryDTO queryDTO;

    @BeforeEach
    void setUp() {
        queryDTO = new BillQueryDTO();
    }

    // -----------------------------------------------------------------------
    // 正常场景
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("正常统计 - 有收入和支出数据，正确计算净收入")
    void getStatistics_withIncomeAndExpense_computesNetIncomeCorrectly() {
        when(billMapper.selectTotalIncome(null, null))
                .thenReturn(new BigDecimal("5000.00"));
        when(billMapper.selectTotalExpense(null, null))
                .thenReturn(new BigDecimal("3000.00"));
        when(billMapper.selectCategoryStatistics(null, null))
                .thenReturn(Arrays.asList(
                        buildCategoryStat(1L, "工资", new BigDecimal("5000.00"), 2),
                        buildCategoryStat(2L, "餐饮", new BigDecimal("3000.00"), 1)
                ));
        when(billMapper.selectPaymentMethodStatistics(null, null))
                .thenReturn(Collections.singletonList(
                        buildPaymentStat(1L, "微信", new BigDecimal("8000.00"))
                ));

        StatisticsVO result = billService.getStatistics(queryDTO);

        assertThat(result.getTotalIncome()).isEqualByComparingTo("5000.00");
        assertThat(result.getTotalExpense()).isEqualByComparingTo("3000.00");
        assertThat(result.getNetIncome()).isEqualByComparingTo("2000.00");
        assertThat(result.getCategoryStatistics()).hasSize(2);
        assertThat(result.getPaymentMethodStatistics()).hasSize(1);
    }

    @Test
    @DisplayName("正常统计 - 无任何数据，各项为 0")
    void getStatistics_emptyData_returnsZero() {
        when(billMapper.selectTotalIncome(null, null))
                .thenReturn(BigDecimal.ZERO);
        when(billMapper.selectTotalExpense(null, null))
                .thenReturn(BigDecimal.ZERO);
        when(billMapper.selectCategoryStatistics(null, null))
                .thenReturn(Collections.emptyList());
        when(billMapper.selectPaymentMethodStatistics(null, null))
                .thenReturn(Collections.emptyList());

        StatisticsVO result = billService.getStatistics(queryDTO);

        assertThat(result.getTotalIncome()).isEqualByComparingTo("0");
        assertThat(result.getTotalExpense()).isEqualByComparingTo("0");
        assertThat(result.getNetIncome()).isEqualByComparingTo("0");
        assertThat(result.getCategoryStatistics()).isEmpty();
        assertThat(result.getPaymentMethodStatistics()).isEmpty();
    }

    @Test
    @DisplayName("正常统计 - 只有支出没有收入，净收入为负数")
    void getStatistics_onlyExpense_netIncomeNegative() {
        when(billMapper.selectTotalIncome(null, null))
                .thenReturn(BigDecimal.ZERO);
        when(billMapper.selectTotalExpense(null, null))
                .thenReturn(new BigDecimal("1500.50"));
        when(billMapper.selectCategoryStatistics(null, null))
                .thenReturn(Collections.emptyList());
        when(billMapper.selectPaymentMethodStatistics(null, null))
                .thenReturn(Collections.emptyList());

        StatisticsVO result = billService.getStatistics(queryDTO);

        assertThat(result.getTotalIncome()).isEqualByComparingTo("0");
        assertThat(result.getTotalExpense()).isEqualByComparingTo("1500.50");
        assertThat(result.getNetIncome()).isEqualByComparingTo("-1500.50");
    }

    // -----------------------------------------------------------------------
    // 边界场景
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("边界 - 带时间范围的查询，参数正确传递到 Mapper")
    void getStatistics_withTimeRange_passesTimeParams() {
        java.util.Date start = new java.util.Date();
        java.util.Date end = new java.util.Date();
        queryDTO.setStartTime(start);
        queryDTO.setEndTime(end);

        when(billMapper.selectTotalIncome(start, end)).thenReturn(BigDecimal.ZERO);
        when(billMapper.selectTotalExpense(start, end)).thenReturn(BigDecimal.ZERO);
        when(billMapper.selectCategoryStatistics(start, end))
                .thenReturn(Collections.emptyList());
        when(billMapper.selectPaymentMethodStatistics(start, end))
                .thenReturn(Collections.emptyList());

        StatisticsVO result = billService.getStatistics(queryDTO);

        verify(billMapper).selectTotalIncome(start, end);
        verify(billMapper).selectTotalExpense(start, end);
        verify(billMapper).selectCategoryStatistics(start, end);
        verify(billMapper).selectPaymentMethodStatistics(start, end);
        assertThat(result).isNotNull();
    }

    // -----------------------------------------------------------------------
    // 异常场景
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("异常 - Mapper 抛异常时，Service 向上透传")
    void getStatistics_mapperThrows_propagatesException() {
        when(billMapper.selectTotalIncome(null, null))
                .thenThrow(new RuntimeException("DB 连接异常"));

        assertThrows(RuntimeException.class,
                () -> billService.getStatistics(queryDTO));
    }

    // -----------------------------------------------------------------------
    // helper
    // -----------------------------------------------------------------------

    private StatisticsVO.CategoryStatistics buildCategoryStat(Long id, String name,
                                                               BigDecimal amount, Integer type) {
        StatisticsVO.CategoryStatistics stat = new StatisticsVO.CategoryStatistics();
        stat.setCategoryId(id);
        stat.setCategoryName(name);
        stat.setAmount(amount);
        stat.setType(type);
        return stat;
    }

    private StatisticsVO.PaymentMethodStatistics buildPaymentStat(Long id, String name,
                                                                   BigDecimal amount) {
        StatisticsVO.PaymentMethodStatistics stat = new StatisticsVO.PaymentMethodStatistics();
        stat.setPaymentMethodId(id);
        stat.setPaymentMethodName(name);
        stat.setAmount(amount);
        return stat;
    }
}