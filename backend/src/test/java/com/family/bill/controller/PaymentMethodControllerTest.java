package com.family.bill.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.family.bill.entity.PaymentMethod;
import com.family.bill.service.PaymentMethodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PaymentMethodController 单元测试 —— 分页接口
 * 使用 MockMvc standalone 方式，避免加载完整 Spring 容器（项目无独立测试 DataSource）
 */
@ExtendWith(MockitoExtension.class)
class PaymentMethodControllerTest {

    @Mock
    private PaymentMethodService paymentMethodService;

    @InjectMocks
    private PaymentMethodController paymentMethodController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentMethodController).build();
    }

    private Page<PaymentMethod> buildOneItemPage() {
        PaymentMethod m = new PaymentMethod();
        m.setId(1L);
        m.setName("微信");
        m.setType(1);
        m.setStatus(1);
        m.setSortOrder(1);

        Page<PaymentMethod> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(m));
        page.setTotal(1);
        return page;
    }

    // -----------------------------------------------------------------------
    // 正常场景
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/payment-methods/page - 默认分页参数正常返回")
    void getPage_default_returns200() throws Exception {
        when(paymentMethodService.getPaymentMethodPage(any())).thenReturn(buildOneItemPage());

        mockMvc.perform(get("/api/payment-methods/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].name").value("微信"));
    }

    @Test
    @DisplayName("GET /api/payment-methods/page - 带查询条件正常返回")
    void getPage_withParams_returns200() throws Exception {
        when(paymentMethodService.getPaymentMethodPage(any())).thenReturn(buildOneItemPage());

        mockMvc.perform(get("/api/payment-methods/page")
                        .param("pageNum", "1")
                        .param("pageSize", "5")
                        .param("name", "微信")
                        .param("type", "1")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].type").value(1));
    }

    // -----------------------------------------------------------------------
    // 边界场景
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/payment-methods/page - 空结果返回空 records")
    void getPage_emptyResult_returnsEmptyRecords() throws Exception {
        Page<PaymentMethod> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        emptyPage.setTotal(0);
        when(paymentMethodService.getPaymentMethodPage(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/payment-methods/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isEmpty());
    }
}
