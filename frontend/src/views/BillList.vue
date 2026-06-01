<template>
  <div class="ledger">
    <!-- 头版：本期总览 -->
    <section class="overview">
      <div class="overview-lead">
        <div class="lead-overline">
          <span class="kicker">本期总览</span>
          <span class="divider" aria-hidden="true"></span>
          <span class="period">{{ periodLabel }}</span>
        </div>

        <div class="lead-net">
          <span class="lead-net-currency">¥</span>
          <span class="lead-net-amount" :class="{ neg: isNetNegative }">{{ formatMoney(statistics.netIncome) }}</span>
        </div>

        <div class="lead-caption">
          <span class="lead-tag" :class="isNetNegative ? 'tag-out' : 'tag-in'">
            {{ isNetNegative ? '净支出' : '净收入' }}
          </span>
          <span class="lead-sub">
            <template v-if="isNetNegative">本期支出大于收入，建议查看主要去向。</template>
            <template v-else>本期收入大于支出，账目盈余。</template>
          </span>
        </div>
      </div>

      <div class="overview-aside">
        <article class="stat-tile in">
          <header>
            <span class="tile-tag">入 · IN</span>
            <span class="tile-arrow">↑</span>
          </header>
          <div class="tile-amount">
            <span class="cur">¥</span>{{ formatMoney(statistics.totalIncome) }}
          </div>
          <footer class="tile-foot">本期合计入账</footer>
        </article>

        <article class="stat-tile out">
          <header>
            <span class="tile-tag">出 · OUT</span>
            <span class="tile-arrow">↓</span>
          </header>
          <div class="tile-amount">
            <span class="cur">¥</span>{{ formatMoney(statistics.totalExpense) }}
          </div>
          <footer class="tile-foot">本期合计支出</footer>
        </article>

        <article class="stat-tile meta">
          <header>
            <span class="tile-tag">记 · LOG</span>
            <span class="tile-arrow">§</span>
          </header>
          <div class="tile-amount mono">
            {{ String(total).padStart(3, '0') }}
            <span class="cur">笔</span>
          </div>
          <footer class="tile-foot">本期登记条目</footer>
        </article>
      </div>
    </section>

    <!-- 工具条 -->
    <section class="toolbar">
      <div class="toolbar-head">
        <h2 class="section-title">
          <span class="num">02</span>
          <span class="zh">流水明细</span>
          <span class="en">Statement of Transactions</span>
        </h2>
        <el-button type="success" @click="handleAdd">
          <span style="font-size: 16px; line-height: 1; margin-right: 6px;">＋</span>
          新增条目
        </el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="交易时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="—"
            start-placeholder="始"
            end-placeholder="终"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" placeholder="不限" clearable @change="handleQuery" style="width: 140px">
            <el-option label="全部" :value="null" />
            <el-option label="收入" :value="2" />
            <el-option label="支出" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="queryForm.paymentMethodId" placeholder="不限" clearable @change="handleQuery" style="width: 160px">
            <el-option v-for="item in paymentMethods" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.categoryId" placeholder="不限" clearable @change="handleQuery" style="width: 160px">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <!-- 流水表 -->
    <section class="ledger-table">
      <el-table :data="billList" style="width: 100%" :show-header="true" :empty-text="'本期暂无记录'">
        <el-table-column prop="payee" label="收付方" min-width="160">
          <template #default="{ row }">
            <div class="cell-payee">
              <span class="dot-mark" :class="row.amount >= 0 ? 'dot-in' : 'dot-out'"></span>
              <span class="payee-name">{{ row.payee }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="amount" label="金额" width="170" align="right">
          <template #default="{ row }">
            <span class="amount" :class="row.amount >= 0 ? 'in' : 'out'">
              <span class="sign">{{ row.amount >= 0 ? '+' : '−' }}</span>
              <span class="cur">¥</span>{{ formatMoney(Math.abs(row.amount)) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="transactionTime" label="交易时间" width="180">
          <template #default="{ row }">
            <div class="cell-time">
              <div class="time-day">{{ dayjs(row.transactionTime).format('MM · DD') }}</div>
              <div class="time-hms">{{ dayjs(row.transactionTime).format('YYYY · HH:mm') }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="paymentMethodName" label="支付方式" width="130">
          <template #default="{ row }">
            <span class="chip">{{ row.paymentMethodName || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="categoryName" label="分类" width="120">
          <template #default="{ row }">
            <span class="chip chip-soft">{{ row.categoryName || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="transactionNo" label="交易号" width="180">
          <template #default="{ row }">
            <span class="mono-id">{{ row.transactionNo || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="remark">{{ row.remark || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="" width="120" fixed="right" align="right">
          <template #default="{ row }">
            <div class="row-actions">
              <button class="link-btn" @click="handleEdit(row)">编辑</button>
              <span class="link-sep">/</span>
              <button class="link-btn danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <div class="page-note">合计 {{ total }} 条记录</div>
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
          background
        />
      </div>
    </section>

    <!-- 新增 / 编辑 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="620px"
      @close="handleDialogClose"
    >
      <el-form :model="billForm" :rules="rules" ref="billFormRef" label-width="100px">
        <el-form-item label="收付方" prop="payee">
          <el-input v-model="billForm.payee" placeholder="例如：星巴克 / 工资 / 房东" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="billForm.amount"
            :precision="2"
            :step="0.01"
            placeholder="支出为负数，收入为正数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="交易时间" prop="transactionTime">
          <el-date-picker
            v-model="billForm.transactionTime"
            type="datetime"
            placeholder="请选择交易时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethodId">
          <el-select v-model="billForm.paymentMethodId" placeholder="请选择支付方式" style="width: 100%">
            <el-option v-for="item in paymentMethods" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易号" prop="transactionNo">
          <el-input v-model="billForm.transactionNo" placeholder="可选" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="billForm.categoryId" placeholder="请选择分类" clearable style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="billForm.remark"
            type="textarea"
            :rows="3"
            placeholder="留下一段说明，未来翻看时会感谢现在的你"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getBillList, createBill, updateBill, deleteBill, getStatistics } from '../api/bill'
import { getPaymentMethods } from '../api/paymentMethod'
import { getCategories } from '../api/category'

// 数据
const billList = ref([])
const total = ref(0)
const paymentMethods = ref([])
const categories = ref([])
const statistics = ref({
  totalIncome: '0.00',
  totalExpense: '0.00',
  netIncome: '0.00'
})
const dateRange = ref([])

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  startTime: null,
  endTime: null,
  type: null,
  paymentMethodId: null,
  categoryId: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增条目')
const billFormRef = ref(null)
const billForm = reactive({
  id: null,
  payee: '',
  amount: null,
  transactionTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
  paymentMethodId: null,
  transactionNo: '',
  categoryId: null,
  remark: ''
})

const rules = {
  payee: [{ required: true, message: '请输入收付方', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  transactionTime: [{ required: true, message: '请选择交易时间', trigger: 'change' }],
  paymentMethodId: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
}

const isNetNegative = computed(() => parseFloat(statistics.value.netIncome || 0) < 0)

const periodLabel = computed(() => {
  if (queryForm.startTime && queryForm.endTime) {
    return `${dayjs(queryForm.startTime).format('YYYY.MM.DD')} — ${dayjs(queryForm.endTime).format('YYYY.MM.DD')}`
  }
  return `${dayjs().format('YYYY 年 MM 月')} · 至今`
})

const formatMoney = (val) => {
  const num = Math.abs(parseFloat(val || 0))
  return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryForm.startTime = dates[0]
    queryForm.endTime = dates[1].split(' ')[0] + ' 23:59:59'
  } else {
    queryForm.startTime = null
    queryForm.endTime = null
  }
  handleQuery()
}

const handleQuery = async () => {
  try {
    const res = await getBillList(queryForm)
    if (res.code === 200) {
      billList.value = res.data.records
      total.value = res.data.total
    }
    await loadStatistics()
  } catch (error) {
    ElMessage.error(error.message || '查询失败')
  }
}

const handleReset = () => {
  queryForm.pageNum = 1
  queryForm.pageSize = 10
  queryForm.startTime = null
  queryForm.endTime = null
  queryForm.type = null
  queryForm.paymentMethodId = null
  queryForm.categoryId = null
  dateRange.value = []
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增条目'
  Object.assign(billForm, {
    id: null,
    payee: '',
    amount: null,
    transactionTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    paymentMethodId: null,
    transactionNo: '',
    categoryId: null,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑条目'
  Object.assign(billForm, {
    id: row.id,
    payee: row.payee,
    amount: parseFloat(row.amount),
    transactionTime: dayjs(row.transactionTime).format('YYYY-MM-DD HH:mm:ss'),
    paymentMethodId: row.paymentMethodId,
    transactionNo: row.transactionNo || '',
    categoryId: row.categoryId,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    const res = await deleteBill(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      handleQuery()
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
  }
}

const handleSubmit = async () => {
  try {
    await billFormRef.value.validate()
    const res = billForm.id
      ? await updateBill(billForm.id, billForm)
      : await createBill(billForm)
    if (res.code === 200) {
      ElMessage.success(billForm.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      handleQuery()
    }
  } catch (error) {
    if (error?.message) ElMessage.error(error.message || '操作失败')
  }
}

const handleDialogClose = () => {
  billFormRef.value?.resetFields()
}

const loadPaymentMethods = async () => {
  try {
    const res = await getPaymentMethods()
    if (res.code === 200) paymentMethods.value = res.data
  } catch (e) { console.error(e) }
}

const loadCategories = async () => {
  try {
    const res = await getCategories()
    if (res.code === 200) categories.value = res.data
  } catch (e) { console.error(e) }
}

const loadStatistics = async () => {
  try {
    const params = { startTime: queryForm.startTime, endTime: queryForm.endTime }
    const res = await getStatistics(params)
    if (res.code === 200) {
      statistics.value = {
        totalIncome: res.data.totalIncome?.toFixed(2) || '0.00',
        totalExpense: res.data.totalExpense?.toFixed(2) || '0.00',
        netIncome: res.data.netIncome?.toFixed(2) || '0.00'
      }
    }
  } catch (e) { console.error(e) }
}

onMounted(() => {
  loadPaymentMethods()
  loadCategories()
  handleQuery()
})
</script>

<style scoped>
.ledger {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeUp 0.6s cubic-bezier(0.2, 0.6, 0.2, 1) both;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ========== 头版 ========== */
.overview {
  display: grid;
  grid-template-columns: 1.25fr 1fr;
  gap: 24px;
  margin-bottom: 44px;
}

.overview-lead {
  position: relative;
  padding: 32px 36px 36px;
  background: var(--card);
  border: 1px solid var(--rule);
  border-radius: var(--radius);
  box-shadow: var(--shadow-1);
  overflow: hidden;
}

.overview-lead::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(800px 240px at 100% 0%, rgba(154, 123, 58, 0.10), transparent 70%),
    radial-gradient(500px 200px at 0% 100%, rgba(31, 93, 76, 0.08), transparent 70%);
  pointer-events: none;
}

.overview-lead::after {
  content: '$';
  position: absolute;
  right: -20px;
  bottom: -60px;
  font-family: var(--font-serif);
  font-style: italic;
  font-weight: 300;
  font-size: 280px;
  line-height: 1;
  color: rgba(28, 26, 23, 0.05);
  pointer-events: none;
}

.lead-overline {
  display: flex;
  align-items: center;
  gap: 14px;
  position: relative;
}

.kicker {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.34em;
  text-transform: uppercase;
  color: var(--ink);
  background: var(--brass-soft);
  padding: 6px 12px;
  border-radius: 999px;
}

.divider {
  flex: 0 0 28px;
  height: 1px;
  background: var(--ink-3);
}

.period {
  font-family: var(--font-serif);
  font-size: 14px;
  letter-spacing: 0.04em;
  color: var(--ink-3);
}

.lead-net {
  position: relative;
  margin-top: 22px;
  display: flex;
  align-items: flex-start;
  gap: 6px;
  line-height: 0.95;
}

.lead-net-currency {
  font-family: var(--font-serif);
  font-weight: 400;
  font-size: 44px;
  color: var(--ink-3);
  margin-top: 18px;
}

.lead-net-amount {
  font-family: var(--font-serif);
  font-weight: 500;
  font-size: 96px;
  letter-spacing: -0.03em;
  color: var(--ink);
  font-feature-settings: 'tnum', 'lnum';
}

.lead-net-amount.neg {
  color: var(--expense);
}

.lead-caption {
  position: relative;
  margin-top: 26px;
  display: flex;
  align-items: center;
  gap: 14px;
  padding-top: 18px;
  border-top: 1px solid var(--rule);
}

.lead-tag {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  padding: 5px 12px;
  border-radius: 999px;
}

.lead-tag.tag-in {
  background: var(--income-soft);
  color: var(--income);
}

.lead-tag.tag-out {
  background: var(--expense-soft);
  color: var(--expense);
}

.lead-sub {
  font-family: var(--font-serif);
  font-style: italic;
  font-size: 14px;
  color: var(--ink-3);
}

/* 副卡片 */
.overview-aside {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

.stat-tile {
  position: relative;
  padding: 18px 22px;
  background: var(--card);
  border: 1px solid var(--rule);
  border-radius: var(--radius);
  box-shadow: var(--shadow-1);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 116px;
  transition: transform 0.2s ease, box-shadow 0.3s ease;
}

.stat-tile:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-2);
}

.stat-tile header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tile-tag {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.28em;
  text-transform: uppercase;
  color: var(--ink-3);
}

.tile-arrow {
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 400;
}

.stat-tile.in .tile-arrow { color: var(--income); }
.stat-tile.out .tile-arrow { color: var(--expense); }
.stat-tile.meta .tile-arrow { color: var(--brass); }

.stat-tile.in {
  border-left: 3px solid var(--income);
}
.stat-tile.out {
  border-left: 3px solid var(--expense);
}
.stat-tile.meta {
  border-left: 3px solid var(--brass);
}

.tile-amount {
  font-family: var(--font-serif);
  font-weight: 500;
  font-size: 32px;
  letter-spacing: -0.02em;
  color: var(--ink);
  font-feature-settings: 'tnum', 'lnum';
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.tile-amount.mono {
  font-family: var(--font-mono);
  font-weight: 500;
  font-size: 30px;
}

.tile-amount .cur {
  font-size: 16px;
  color: var(--ink-3);
  font-weight: 400;
}

.tile-foot {
  font-size: 12px;
  color: var(--ink-mute);
  letter-spacing: 0.04em;
}

/* ========== 工具条 ========== */
.toolbar {
  margin-bottom: 22px;
}

.toolbar-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 18px;
}

.section-title {
  display: flex;
  align-items: baseline;
  gap: 14px;
  line-height: 1;
}

.section-title .num {
  font-family: var(--font-serif);
  font-style: italic;
  font-weight: 300;
  font-size: 22px;
  color: var(--brass);
}

.section-title .zh {
  font-family: var(--font-serif);
  font-weight: 600;
  font-size: 26px;
  letter-spacing: 0.04em;
  color: var(--ink);
}

.section-title .en {
  font-family: var(--font-serif);
  font-style: italic;
  font-weight: 300;
  font-size: 15px;
  color: var(--ink-3);
}

.filter-form {
  padding: 16px 22px;
  background: var(--paper-tint);
  border: 1px dashed var(--rule);
  border-radius: var(--radius);
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 14px;
}

.filter-form :deep(.el-form-item__label) {
  padding-right: 8px;
}

/* ========== 表格 ========== */
.ledger-table {
  padding: 4px 4px 16px;
}

.cell-payee {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dot-mark {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex: 0 0 auto;
  box-shadow: 0 0 0 3px var(--paper-tint);
}

.dot-in { background: var(--income); }
.dot-out { background: var(--expense); }

.payee-name {
  font-weight: 500;
  color: var(--ink);
  letter-spacing: 0.01em;
}

.amount {
  font-family: var(--font-serif);
  font-weight: 500;
  font-size: 18px;
  letter-spacing: -0.01em;
  font-feature-settings: 'tnum', 'lnum';
  display: inline-flex;
  align-items: baseline;
  gap: 2px;
}

.amount.in { color: var(--income); }
.amount.out { color: var(--expense); }

.amount .sign {
  font-size: 16px;
  margin-right: 2px;
}

.amount .cur {
  font-size: 12px;
  font-family: var(--font-sans);
  font-weight: 500;
  margin-right: 1px;
  opacity: 0.7;
}

.cell-time {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.time-day {
  font-family: var(--font-serif);
  font-weight: 500;
  font-size: 15px;
  color: var(--ink);
  letter-spacing: 0.04em;
}

.time-hms {
  font-family: var(--font-mono);
  font-size: 11px;
  color: var(--ink-mute);
}

.chip {
  display: inline-block;
  padding: 4px 10px;
  background: var(--paper-2);
  border: 1px solid var(--rule);
  border-radius: 999px;
  font-size: 12px;
  color: var(--ink-2);
  letter-spacing: 0.02em;
}

.chip-soft {
  background: transparent;
  border-color: var(--rule);
  color: var(--ink-3);
}

.mono-id {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--ink-3);
  letter-spacing: 0.02em;
}

.remark {
  font-family: var(--font-serif);
  font-style: italic;
  font-size: 14px;
  color: var(--ink-2);
}

.row-actions {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.link-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-family: var(--font-sans);
  font-size: 13px;
  color: var(--ink-2);
  padding: 4px 2px;
  letter-spacing: 0.02em;
  border-bottom: 1px solid transparent;
  transition: border-color 0.15s ease, color 0.15s ease;
}

.link-btn:hover {
  color: var(--ink);
  border-bottom-color: var(--ink);
}

.link-btn.danger:hover {
  color: var(--expense);
  border-bottom-color: var(--expense);
}

.link-sep {
  color: var(--rule);
  font-size: 12px;
}

/* 分页 */
.pagination-bar {
  margin-top: 22px;
  padding: 16px 4px 0;
  border-top: 1px solid var(--ink);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-note {
  font-family: var(--font-serif);
  font-style: italic;
  font-size: 13px;
  color: var(--ink-3);
}

/* 响应式 */
@media (max-width: 1080px) {
  .overview {
    grid-template-columns: 1fr;
  }
  .lead-net-amount {
    font-size: 72px;
  }
  .overview-aside {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 720px) {
  .overview-aside {
    grid-template-columns: 1fr;
  }
  .lead-net-amount {
    font-size: 56px;
  }
  .lead-net-currency {
    font-size: 28px;
    margin-top: 10px;
  }
  .toolbar-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }
  .pagination-bar {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
