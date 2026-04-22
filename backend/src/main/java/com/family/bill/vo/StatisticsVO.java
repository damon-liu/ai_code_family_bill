package com.family.bill.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 统计信息视图对象
 * 
 * @author family-bill
 */
@Data
public class StatisticsVO {
    
    /**
     * 总收入
     */
    private BigDecimal totalIncome;
    
    /**
     * 总支出
     */
    private BigDecimal totalExpense;
    
    /**
     * 净收入（收入-支出）
     */
    private BigDecimal netIncome;
    
    /**
     * 按分类统计
     */
    private List<CategoryStatistics> categoryStatistics;
    
    /**
     * 按支付方式统计
     */
    private List<PaymentMethodStatistics> paymentMethodStatistics;
    
    @Data
    public static class CategoryStatistics {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
        private Integer type;
    }
    
    @Data
    public static class PaymentMethodStatistics {
        private Long paymentMethodId;
        private String paymentMethodName;
        private BigDecimal amount;
    }
}


