<template>
  <div class="bill-list-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="8">
        <el-card class="stat-card income">
          <div class="stat-content">
            <div class="stat-label">总收入</div>
            <div class="stat-value income-value">¥{{ statistics.totalIncome || '0.00' }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card expense">
          <div class="stat-content">
            <div class="stat-label">总支出</div>
            <div class="stat-value expense-value">¥{{ statistics.totalExpense || '0.00' }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card net">
          <div class="stat-content">
            <div class="stat-label">净收入</div>
            <div class="stat-value" :class="netIncomeClass">¥{{ statistics.netIncome || '0.00' }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-card class="operation-card">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="交易时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" placeholder="请选择" clearable @change="handleQuery">
            <el-option label="全部" :value="null" />
            <el-option label="收入" :value="2" />
            <el-option label="支出" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="queryForm.paymentMethodId" placeholder="请选择" clearable @change="handleQuery">
            <el-option
              v-for="item in paymentMethods"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.categoryId" placeholder="请选择" clearable @change="handleQuery">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">新增账单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 账单列表 -->
    <el-card class="table-card">
      <el-table :data="billList" stripe style="width: 100%">
        <el-table-column prop="payee" label="收款方" width="150" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="row.amount >= 0 ? 'income-text' : 'expense-text'">
              {{ row.amount >= 0 ? '+' : '' }}¥{{ row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="transactionTime" label="交易时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.transactionTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethodName" label="支付方式" width="120" />
        <el-table-column prop="transactionNo" label="交易号" width="180" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="billForm" :rules="rules" ref="billFormRef" label-width="100px">
        <el-form-item label="收款方" prop="payee">
          <el-input v-model="billForm.payee" placeholder="请输入收款方" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="billForm.amount"
            :precision="2"
            :step="0.01"
            placeholder="请输入金额（支出为负数，收入为正数）"
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
            <el-option
              v-for="item in paymentMethods"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="交易号" prop="transactionNo">
          <el-input v-model="billForm.transactionNo" placeholder="请输入交易号（可选）" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="billForm.categoryId" placeholder="请选择分类" clearable style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="billForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（可选）"
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

// 查询表单
const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  startTime: null,
  endTime: null,
  type: null,
  paymentMethodId: null,
  categoryId: null
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增账单')
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

// 表单验证规则
const rules = {
  payee: [{ required: true, message: '请输入收款方', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  transactionTime: [{ required: true, message: '请选择交易时间', trigger: 'change' }],
  paymentMethodId: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
}

// 计算属性
const netIncomeClass = computed(() => {
  const net = parseFloat(statistics.value.netIncome || 0)
  return net >= 0 ? 'income-value' : 'expense-value'
})

// 方法
const formatDateTime = (dateTime) => {
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryForm.startTime = dates[0]
    queryForm.endTime = dates[1] + ' 23:59:59'
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
  dialogTitle.value = '新增账单'
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
  dialogTitle.value = '编辑账单'
  Object.assign(billForm, {
    id: row.id,
    payee: row.payee,
    amount: parseFloat(row.amount),
    transactionTime: formatDateTime(row.transactionTime),
    paymentMethodId: row.paymentMethodId,
    transactionNo: row.transactionNo || '',
    categoryId: row.categoryId,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条账单吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteBill(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      handleQuery()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
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
    ElMessage.error(error.message || '操作失败')
  }
}

const handleDialogClose = () => {
  billFormRef.value?.resetFields()
}

const loadPaymentMethods = async () => {
  try {
    const res = await getPaymentMethods()
    if (res.code === 200) {
      paymentMethods.value = res.data
    }
  } catch (error) {
    console.error('加载支付方式失败', error)
  }
}

const loadCategories = async () => {
  try {
    const res = await getCategories()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const loadStatistics = async () => {
  try {
    const params = {
      startTime: queryForm.startTime,
      endTime: queryForm.endTime
    }
    const res = await getStatistics(params)
    if (res.code === 200) {
      statistics.value = {
        totalIncome: res.data.totalIncome?.toFixed(2) || '0.00',
        totalExpense: res.data.totalExpense?.toFixed(2) || '0.00',
        netIncome: res.data.netIncome?.toFixed(2) || '0.00'
      }
    }
  } catch (error) {
    console.error('加载统计信息失败', error)
  }
}

// 生命周期
onMounted(() => {
  loadPaymentMethods()
  loadCategories()
  handleQuery()
})
</script>

<style scoped>
.bill-list-container {
  max-width: 1400px;
  margin: 0 auto;
}

.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.income-value {
  color: #67c23a;
}

.expense-value {
  color: #f56c6c;
}

.operation-card {
  margin-bottom: 20px;
}

.query-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.income-text {
  color: #67c23a;
  font-weight: bold;
}

.expense-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>


