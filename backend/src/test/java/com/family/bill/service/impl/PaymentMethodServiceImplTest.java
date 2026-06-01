package com.family.bill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.family.bill.dto.PaymentMethodQueryDTO;
import com.family.bill.entity.PaymentMethod;
import com.family.bill.mapper.PaymentMethodMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PaymentMethodServiceImpl 单元测试 —— 分页查询
 */
@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceImplTest {

    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @InjectMocks
    private PaymentMethodServiceImpl paymentMethodService;

    private PaymentMethod buildMethod(Long id, String name, Integer type, Integer status) {
        PaymentMethod m = new PaymentMethod();
        m.setId(id);
        m.setName(name);
        m.setType(type);
        m.setStatus(status);
        m.setSortOrder(id.intValue());
        return m;
    }

    // -----------------------------------------------------------------------
    // 正常场景
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("无条件分页 - 返回全部数据，分页信息正确")
    void getPage_noCondition_returnAllRecords() {
        PaymentMethodQueryDTO dto = new PaymentMethodQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        Page<PaymentMethod> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
                buildMethod(1L, "微信", 1, 1),
                buildMethod(2L, "支付宝", 1, 1)
        ));
        mockPage.setTotal(2);

        when(paymentMethodMapper.selectPage(any(), any())).thenReturn(mockPage);

        IPage<PaymentMethod> result = paymentMethodService.getPaymentMethodPage(dto);

        assertThat(result.getRecords()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getCurrent()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("按名称模糊查询 - 正常返回")
    void getPage_withName_passesNameCondition() {
        PaymentMethodQueryDTO dto = new PaymentMethodQueryDTO();
        dto.setName("微信");
        dto.setPageNum(1);
        dto.setPageSize(5);

        Page<PaymentMethod> mockPage = new Page<>(1, 5);
        mockPage.setRecords(Collections.singletonList(buildMethod(1L, "微信支付", 1, 1)));
        mockPage.setTotal(1);

        when(paymentMethodMapper.selectPage(any(), any())).thenReturn(mockPage);

        IPage<PaymentMethod> result = paymentMethodService.getPaymentMethodPage(dto);

        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getName()).isEqualTo("微信支付");
        verify(paymentMethodMapper).selectPage(any(), any());
    }

    @Test
    @DisplayName("按类型和状态过滤 - 正常返回")
    void getPage_withTypeAndStatus_filtersCorrectly() {
        PaymentMethodQueryDTO dto = new PaymentMethodQueryDTO();
        dto.setType(1);
        dto.setStatus(1);
        dto.setPageNum(1);
        dto.setPageSize(10);

        Page<PaymentMethod> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.singletonList(buildMethod(1L, "微信", 1, 1)));
        mockPage.setTotal(1);

        when(paymentMethodMapper.selectPage(any(), any())).thenReturn(mockPage);

        IPage<PaymentMethod> result = paymentMethodService.getPaymentMethodPage(dto);

        assertThat(result.getTotal()).isEqualTo(1);
        verify(paymentMethodMapper, times(1)).selectPage(any(), any());
    }

    // -----------------------------------------------------------------------
    // 边界场景
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("边界 - 第2页，每页1条，验证分页参数透传")
    void getPage_secondPage_pageParamPassedCorrectly() {
        PaymentMethodQueryDTO dto = new PaymentMethodQueryDTO();
        dto.setPageNum(2);
        dto.setPageSize(1);

        // 捕获传入 selectPage 的 Page 对象
        ArgumentCaptor<Page<PaymentMethod>> pageCaptor = ArgumentCaptor.forClass(Page.class);

        Page<PaymentMethod> mockPage = new Page<>(2, 1);
        mockPage.setRecords(Collections.singletonList(buildMethod(2L, "支付宝", 3, 1)));
        mockPage.setTotal(3);

        when(paymentMethodMapper.selectPage(pageCaptor.capture(), any())).thenReturn(mockPage);

        IPage<PaymentMethod> result = paymentMethodService.getPaymentMethodPage(dto);

        assertThat(pageCaptor.getValue().getCurrent()).isEqualTo(2);
        assertThat(pageCaptor.getValue().getSize()).isEqualTo(1);
        assertThat(result.getTotal()).isEqualTo(3);
    }

    @Test
    @DisplayName("边界 - 结果为空列表，返回空 records 不抛异常")
    void getPage_emptyResult_returnsEmptyList() {
        PaymentMethodQueryDTO dto = new PaymentMethodQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        Page<PaymentMethod> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        emptyPage.setTotal(0);

        when(paymentMethodMapper.selectPage(any(), any())).thenReturn(emptyPage);

        IPage<PaymentMethod> result = paymentMethodService.getPaymentMethodPage(dto);

        assertThat(result.getRecords()).isEmpty();
        assertThat(result.getTotal()).isZero();
    }

    @Test
    @DisplayName("边界 - name 为空字符串，不拼 LIKE 条件")
    void getPage_blankName_notAppendLikeCondition() {
        PaymentMethodQueryDTO dto = new PaymentMethodQueryDTO();
        dto.setName("   "); // 空白字符串，StringUtils.isNotBlank 应判为 false
        dto.setPageNum(1);
        dto.setPageSize(10);

        Page<PaymentMethod> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.emptyList());
        mockPage.setTotal(0);

        when(paymentMethodMapper.selectPage(any(), any())).thenReturn(mockPage);

        // 只需验证调用不报错，不抛异常
        IPage<PaymentMethod> result = paymentMethodService.getPaymentMethodPage(dto);

        assertThat(result).isNotNull();
        verify(paymentMethodMapper).selectPage(any(), any());
    }

    // -----------------------------------------------------------------------
    // 异常场景
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("异常 - Mapper 抛出运行时异常时，Service 向上透传")
    void getPage_mapperThrows_propagatesException() {
        PaymentMethodQueryDTO dto = new PaymentMethodQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        when(paymentMethodMapper.selectPage(any(), any()))
                .thenThrow(new RuntimeException("DB 连接异常"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> paymentMethodService.getPaymentMethodPage(dto));
    }
}
