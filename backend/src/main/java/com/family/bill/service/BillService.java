package com.family.bill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.bill.dto.BillDTO;
import com.family.bill.dto.BillQueryDTO;
import com.family.bill.vo.BillVO;
import com.family.bill.vo.StatisticsVO;

/**
 * 账单服务接口
 * 
 * @author family-bill
 */
public interface BillService {
    
    /**
     * 分页查询账单列表
     */
    IPage<BillVO> getBillPage(BillQueryDTO queryDTO);
    
    /**
     * 根据ID查询账单
     */
    BillVO getBillById(Long id);
    
    /**
     * 新增账单
     */
    Long createBill(BillDTO billDTO);
    
    /**
     * 更新账单
     */
    void updateBill(Long id, BillDTO billDTO);
    
    /**
     * 删除账单
     */
    void deleteBill(Long id);
    
    /**
     * 获取统计信息
     */
    StatisticsVO getStatistics(BillQueryDTO queryDTO);
}


