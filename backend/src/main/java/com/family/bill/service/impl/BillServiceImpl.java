package com.family.bill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.family.bill.dto.BillDTO;
import com.family.bill.dto.BillQueryDTO;
import com.family.bill.entity.Bill;
import com.family.bill.mapper.BillMapper;
import com.family.bill.service.BillService;
import com.family.bill.vo.BillVO;
import com.family.bill.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账单服务实现类
 * 
 * @author family-bill
 */
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    
    private final BillMapper billMapper;
    
    @Override
    //@Cacheable(value = "bill", key = "'page:' + #queryDTO.pageNum + ':' + #queryDTO.pageSize + ':' + (#queryDTO.startTime != null ? #queryDTO.startTime.toString() : 'null') + ':' + (#queryDTO.endTime != null ? #queryDTO.endTime.toString() : 'null')")
    public IPage<BillVO> getBillPage(BillQueryDTO queryDTO) {
        Page<BillVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return billMapper.selectBillPage(page, queryDTO.getStartTime(), queryDTO.getEndTime(),
                queryDTO.getPaymentMethodId(), queryDTO.getCategoryId(), queryDTO.getType());
    }
    
    @Override
    @Cacheable(value = "bill", key = "'id:' + #id")
    public BillVO getBillById(Long id) {
        BillVO billVO = billMapper.selectBillById(id);
        if (billVO == null) {
            throw new RuntimeException("账单不存在");
        }
        return billVO;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"bill", "statistics"}, allEntries = true)
    public Long createBill(BillDTO billDTO) {
        Bill bill = new Bill();
        BeanUtils.copyProperties(billDTO, bill);
        bill.setUserId(1L); // 默认用户ID，后续可扩展
        
        billMapper.insert(bill);
        return bill.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"bill", "statistics"}, allEntries = true)
    public void updateBill(Long id, BillDTO billDTO) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            throw new RuntimeException("账单不存在");
        }
        
        BeanUtils.copyProperties(billDTO, bill, "id");
        billMapper.updateById(bill);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"bill", "statistics"}, allEntries = true)
    public void deleteBill(Long id) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            throw new RuntimeException("账单不存在");
        }
        billMapper.deleteById(id);
    }
    
    @Override
    @Cacheable(value = "statistics", key = "'stats:' + (#queryDTO.startTime != null ? #queryDTO.startTime.toString() : 'null') + ':' + (#queryDTO.endTime != null ? #queryDTO.endTime.toString() : 'null')")
    public StatisticsVO getStatistics(BillQueryDTO queryDTO) {
        StatisticsVO statistics = new StatisticsVO();
        
        // 统计总收入
        statistics.setTotalIncome(billMapper.selectTotalIncome(queryDTO.getStartTime(), queryDTO.getEndTime()));
        
        // 统计总支出
        statistics.setTotalExpense(billMapper.selectTotalExpense(queryDTO.getStartTime(), queryDTO.getEndTime()));
        
        // 计算净收入
        statistics.setNetIncome(statistics.getTotalIncome().subtract(statistics.getTotalExpense()));
        
        // 按分类统计
        List<StatisticsVO.CategoryStatistics> categoryStatistics = billMapper.selectCategoryStatistics(
                queryDTO.getStartTime(), queryDTO.getEndTime());
        statistics.setCategoryStatistics(categoryStatistics);
        
        // 按支付方式统计
        List<StatisticsVO.PaymentMethodStatistics> paymentMethodStatistics = billMapper.selectPaymentMethodStatistics(
                queryDTO.getStartTime(), queryDTO.getEndTime());
        statistics.setPaymentMethodStatistics(paymentMethodStatistics);
        
        return statistics;
    }
}

